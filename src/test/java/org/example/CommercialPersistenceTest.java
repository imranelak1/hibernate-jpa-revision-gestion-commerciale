package org.example;
import org.example.dao.GenericDAO;
import org.example.entities.*;
import org.example.service.*;
import org.example.util.HibernateUtil;
import org.example.web.RapportVentesServlet;
import org.hibernate.SessionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.junit.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.Assert.*;

public class CommercialPersistenceTest {
    private SessionFactory emf;
    private GenericDAO<Categorie,Long> categories;
    private ProduitService produits;
    @Before public void init() { emf=HibernateUtil.buildSessionFactory("revision_test_"+UUID.randomUUID().toString().replace("-",""),Map.of());categories=new GenericDAO<>(Categorie.class,emf);produits=new ProduitService(emf); }
    @After public void close() { emf.close(); }
    private Produit produit(String name,String price,Categorie cat) { Produit p=new Produit(name,new BigDecimal(price),cat);produits.create(p);return p; }
    @Test public void crudEtCategorieAccessibleApresFermetureDuContexte() {
        Categorie cat=new Categorie("Papeterie");categories.create(cat);Produit p=produit("Stylo","1.20",cat);
        assertEquals("Papeterie",produits.getById(p.getId()).getCategorie().getNom());
        p.setLibelle("Stylo bleu");produits.update(p);assertEquals("Stylo bleu",produits.getById(p.getId()).getLibelle());
        produits.delete(p);assertNull(produits.getById(p.getId()));
    }
    @Test public void requetesNommeesCriteriaEtPagination() {
        Categorie cat=new Categorie("Informatique");categories.create(cat);
        Produit clavier=produit("Clavier","49.99",cat);produit("Souris","20.00",cat);produit("Ecran","500.00",cat);
        assertEquals(3,produits.findByCategorie(cat.getId()).size());assertEquals(clavier.getId(),produits.searchByLibelle("CLAV").get(0).getId());
        assertEquals(2,produits.search(null,new BigDecimal("20"),new BigDecimal("49.99")).size());
        assertEquals(1,produits.findPage(2,2).size());assertEquals(0,produits.findPage(3,2).size());
        assertThrows(IllegalArgumentException.class,()->produits.findPage(0,2));assertThrows(IllegalArgumentException.class,()->produits.search(null,BigDecimal.TEN,BigDecimal.ONE));
        assertEquals(0,produits.searchByLibelle("' OR '1'='1").size());
    }
    @Test public void rollbackNeLaisseAucuneEcritureEtFermeLesSessions() {
        long opened=emf.getStatistics().getSessionOpenCount(),closed=emf.getStatistics().getSessionCloseCount();
        assertThrows(IllegalStateException.class,()->categories.transaction(em->{em.persist(new Categorie("Annulee"));throw new IllegalStateException("rollback");}));
        assertEquals(0,categories.findAll().size());
        assertEquals(emf.getStatistics().getSessionOpenCount()-opened,emf.getStatistics().getSessionCloseCount()-closed);
        Categorie cat=new Categorie("Unique");categories.create(cat);
        assertThrows(javax.persistence.PersistenceException.class,()->categories.create(new Categorie("Unique")));
        assertEquals(1,categories.findAll().size());
    }
    @Test public void heritageJoinedEtCleComposeePersistants() {
        GenericDAO<User,Long> users=new GenericDAO<>(User.class,emf);users.create(new Client("Client",LocalDate.of(2026,9,1)));users.create(new Fournisseur("Fournisseur","Casa"));
        assertEquals(2,users.findAll().size());assertTrue(users.findAll().get(0) instanceof Client);assertTrue(users.findAll().get(1) instanceof Fournisseur);
        Categorie cat=new Categorie("Stock");categories.create(cat);Produit p=produit("Article","20",cat);
        Commande cmd=new Commande(LocalDate.of(2026,9,15));cmd.addProduit(p,3);new CommandeService(emf).create(cmd);
        var ligne=new GenericDAO<CommandeProduit,CommandeProduitPK>(CommandeProduit.class,emf).find(new CommandeProduitPK(cmd.getId(),p.getId()));
        assertNotNull(ligne);assertEquals(3,ligne.getQuantite());assertEquals(new CommandeProduitPK(cmd.getId(),p.getId()),ligne.getId());
        assertThrows(IllegalArgumentException.class,()->cmd.addProduit(p,4));
    }
    @Test public void orphanRemovalSupprimeLeProduitRetire() {
        Categorie cat=new Categorie("Cascade");Produit p=new Produit("Orphelin",BigDecimal.ONE,cat);cat.addProduit(p);categories.create(cat);
        categories.transaction(em->{Categorie managed=em.find(Categorie.class,cat.getId());managed.removeProduit(managed.getProduits().get(0));return null;});
        assertNull(produits.getById(p.getId()));
    }
    private void seedRapport() {
        Categorie cat=new Categorie("Informatique");categories.create(cat);Produit p=produit("Clavier","49.99",cat);
        var dao=new CommandeService(emf);
        for(var pair:List.of(Map.entry("2026-08-31",100),Map.entry("2026-09-01",3),Map.entry("2026-09-30",5),Map.entry("2026-10-01",100))) {
            Commande cmd=new Commande(LocalDate.parse(pair.getKey()));cmd.addProduit(p,pair.getValue());dao.create(cmd);
        }
    }
    @Test public void rapportCriteriaAgregeSeulementLeMoisPrecedent() {
        seedRapport();var result=new RapportVentesService(emf).moisPrecedent(LocalDate.of(2026,10,11));
        assertEquals(1,result.size());assertEquals("Informatique",result.get(0).categorie());assertEquals(0,new BigDecimal("399.92").compareTo(result.get(0).chiffreAffaires()));
    }
    @Test public void servletRenvoieLeRapportReelEtRejetteLaDateInvalide() throws Exception {
        seedRapport();Server server=new Server(0);ServletContextHandler context=new ServletContextHandler();context.addServlet(new ServletHolder(new RapportVentesServlet(emf)),"/rapport-ventes");server.setHandler(context);
        try {
            server.start();int port=((ServerConnector)server.getConnectors()[0]).getLocalPort();
            HttpURLConnection response=(HttpURLConnection)new URI("http://127.0.0.1:"+port+"/rapport-ventes?date=2026-10-11").toURL().openConnection();
            assertEquals(200,response.getResponseCode());try(var input=response.getInputStream()) { String json=new String(input.readAllBytes(),StandardCharsets.UTF_8);assertTrue(json.contains("2026-09"));assertTrue(json.contains("399.92")); }response.disconnect();
            response=(HttpURLConnection)new URI("http://127.0.0.1:"+port+"/rapport-ventes?date=bad").toURL().openConnection();assertEquals(400,response.getResponseCode());response.disconnect();
        } finally { server.stop();server.destroy(); }
    }
}
