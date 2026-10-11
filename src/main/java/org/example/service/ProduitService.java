package org.example.service;
import org.example.entities.Produit;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

public class ProduitService extends CrudService<Produit,Long> {
    public ProduitService(EntityManagerFactory emf) { super(Produit.class,emf); }
    public List<Produit> findByCategorie(Long id) { return dao.read(em->em.createNamedQuery("Produit.findByCategorie",Produit.class).setParameter("catId",id).getResultList()); }
    public List<Produit> searchByLibelle(String term) { return dao.read(em->em.createNamedQuery("Produit.searchByLibelle",Produit.class).setParameter("term",term).getResultList()); }
    @Override public Produit getById(Long id) { return dao.read(em->em.createQuery("select p from Produit p join fetch p.categorie where p.id=:id",Produit.class).setParameter("id",id).getResultStream().findFirst().orElse(null)); }
    public List<Produit> search(String term,BigDecimal min,BigDecimal max) {
        if(min!=null&&max!=null&&min.compareTo(max)>0)throw new IllegalArgumentException("Intervalle de prix inverse");
        return dao.read(em->{
            CriteriaBuilder cb=em.getCriteriaBuilder();CriteriaQuery<Produit> cq=cb.createQuery(Produit.class);Root<Produit> root=cq.from(Produit.class);
            List<Predicate> predicates=new ArrayList<>();
            if(term!=null&&!term.isBlank())predicates.add(cb.like(cb.lower(root.get("libelle")),"%"+term.toLowerCase(Locale.ROOT)+"%"));
            if(min!=null)predicates.add(cb.greaterThanOrEqualTo(root.get("prix"),min));
            if(max!=null)predicates.add(cb.lessThanOrEqualTo(root.get("prix"),max));
            cq.where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("id")));
            return em.createQuery(cq).getResultList();
        });
    }
    public List<Produit> findPage(int page,int size) {
        if(page<1||size<1)throw new IllegalArgumentException("Page et taille positives requises");
        int offset=Math.multiplyExact(page-1,size);
        return dao.read(em->em.createQuery("select p from Produit p order by p.id",Produit.class).setFirstResult(offset).setMaxResults(size).getResultList());
    }
}
