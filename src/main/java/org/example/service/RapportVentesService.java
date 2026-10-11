package org.example.service;
import org.example.dao.GenericDAO;
import org.example.entities.*;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

public class RapportVentesService {
    public record VenteCategorie(String categorie,BigDecimal chiffreAffaires) {}
    private final GenericDAO<Commande,Long> dao;
    public RapportVentesService(EntityManagerFactory emf) { dao=new GenericDAO<>(Commande.class,emf); }
    public List<VenteCategorie> moisPrecedent(LocalDate reference) {
        LocalDate debut=reference.withDayOfMonth(1).minusMonths(1),fin=reference.withDayOfMonth(1);
        return dao.read(em->{
            CriteriaBuilder cb=em.getCriteriaBuilder();CriteriaQuery<Tuple> cq=cb.createTupleQuery();Root<CommandeProduit> ligne=cq.from(CommandeProduit.class);
            Join<CommandeProduit,Produit> produit=ligne.join("produit");Join<Produit,Categorie> categorie=produit.join("categorie");
            Expression<BigDecimal> montant=cb.prod(produit.<BigDecimal>get("prix"),ligne.<Integer>get("quantite")).as(BigDecimal.class);
            cq.multiselect(categorie.get("nom").alias("categorie"),cb.sum(montant).alias("ca"));
            Path<LocalDate> date=ligne.get("commande").get("dateCommande");
            cq.where(cb.greaterThanOrEqualTo(date,debut),cb.lessThan(date,fin));
            cq.groupBy(categorie.get("id"),categorie.get("nom")).orderBy(cb.asc(categorie.get("nom")));
            return em.createQuery(cq).getResultList().stream().map(t->new VenteCategorie(t.get("categorie",String.class),t.get("ca",BigDecimal.class))).toList();
        });
    }
}
