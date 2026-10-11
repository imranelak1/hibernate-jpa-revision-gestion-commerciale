package org.example;
import org.example.dao.GenericDAO;
import org.example.entities.*;
import org.example.service.*;
import org.example.util.HibernateUtil;
import org.example.web.RapportVentesServlet;
import org.hibernate.SessionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) throws Exception {
        SessionFactory emf=HibernateUtil.getSessionFactory();
        try {
            System.out.println("REVISION — GESTION COMMERCIALE : CRUD, CRITERIA, JOINED, CLE COMPOSEE");
            var categories=new CrudService<Categorie,Long>(Categorie.class,emf);
            var produits=new ProduitService(emf);
            Categorie informatique=new Categorie("Informatique");categories.create(informatique);
            Categorie papeterie=new Categorie("Papeterie");categories.create(papeterie);
            Produit clavier=new Produit("Clavier",new BigDecimal("49.99"),informatique);produits.create(clavier);
            Produit souris=new Produit("Souris",new BigDecimal("20.00"),informatique);produits.create(souris);
            Produit stylo=new Produit("Stylo",new BigDecimal("1.20"),papeterie);produits.create(stylo);
            System.out.println("Categorie creee : "+categories.getById(informatique.getId()));
            System.out.println("Produits par categorie : "+produits.findByCategorie(informatique.getId()));
            System.out.println("Recherche nommee CLAV : "+produits.searchByLibelle("CLAV"));
            System.out.println("Criteria prix 10..50 : "+produits.search(null,new BigDecimal("10"),new BigDecimal("50")));
            System.out.println("Pagination page 2 taille 2 : "+produits.findPage(2,2));
            var users=new CrudService<User,Long>(User.class,emf);
            users.create(new Client("Imrane",LocalDate.of(2026,9,1)));users.create(new Fournisseur("Toubkal", "Casablanca"));
            System.out.println("Heritage JOINED : "+users.getAll().stream().map(u->u.getClass().getSimpleName()+":"+u.getNom()).toList());
            var commandes=new CommandeService(emf);
            Commande commande=new Commande(LocalDate.of(2026,9,15));commande.addProduit(clavier,3);commande.addProduit(souris,5);commande.addProduit(stylo,10);commandes.create(commande);
            System.out.println("Commande "+commande.getId()+" : 3 lignes a cle composee persistantes");
            var dao=new GenericDAO<Commande,Long>(Commande.class,emf);
            try { dao.transaction(em->{em.persist(new Categorie("Annulee"));throw new IllegalStateException("Erreur simulee");}); }
            catch(IllegalStateException e) { System.out.println("Rollback : "+e.getMessage()+" ; categories restantes="+categories.getAll().size()); }
            var rapport=new RapportVentesService(emf).moisPrecedent(LocalDate.of(2026,10,11));
            System.out.println("Rapport Criteria septembre 2026 : "+rapport);
            boolean serve=java.util.Arrays.asList(args).contains("--serve");
            Server server=new Server(serve?8087:0);ServletContextHandler context=new ServletContextHandler();context.setContextPath("/");context.addServlet(new ServletHolder(new RapportVentesServlet(emf)),"/rapport-ventes");server.setHandler(context);
            try {
                server.start();int port=((ServerConnector)server.getConnectors()[0]).getLocalPort();
                HttpURLConnection connection=(HttpURLConnection)new URI("http://127.0.0.1:"+port+"/rapport-ventes?date=2026-10-11").toURL().openConnection();
                System.out.println("Servlet GET /rapport-ventes : HTTP "+connection.getResponseCode());
                try(var input=connection.getInputStream()) { System.out.println(new String(input.readAllBytes(),StandardCharsets.UTF_8)); }
                connection.disconnect();
                if(serve) { System.out.println("Serveur disponible : http://127.0.0.1:8087/rapport-ventes?date=2026-10-11 ; Ctrl+C pour arreter.");server.join(); }
            } finally { server.stop();server.destroy(); }
        } finally { HibernateUtil.shutdown(); }
    }
}
