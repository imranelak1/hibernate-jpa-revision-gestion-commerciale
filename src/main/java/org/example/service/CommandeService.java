package org.example.service;
import org.example.entities.*;
import javax.persistence.EntityManagerFactory;

public class CommandeService extends CrudService<Commande,Long> {
    public CommandeService(EntityManagerFactory emf) { super(Commande.class,emf); }
    @Override public void create(Commande commande) {
        if(commande==null)throw new IllegalArgumentException("Commande requise");
        dao.transaction(em->{
            // MapsId derives the FK from an association in this persistence context.
            for(CommandeProduit ligne:commande.getLignes()) {
                Long id=ligne.getProduit().getId();
                if(id==null)throw new IllegalArgumentException("Produit persistant requis");
                ligne.setProduit(em.getReference(Produit.class,id));
            }
            em.persist(commande);return null;
        });
    }
}
