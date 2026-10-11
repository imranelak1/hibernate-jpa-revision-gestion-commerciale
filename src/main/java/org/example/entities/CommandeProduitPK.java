package org.example.entities;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommandeProduitPK implements Serializable {
    @Column(name="commande_id") private Long commandeId;
    @Column(name="produit_id") private Long produitId;
    public CommandeProduitPK() {}
    public CommandeProduitPK(Long commandeId,Long produitId) { this.commandeId=commandeId;this.produitId=produitId; }
    public Long getCommandeId() { return commandeId; }
    public void setCommandeId(Long id) { commandeId=id; }
    public Long getProduitId() { return produitId; }
    public void setProduitId(Long id) { produitId=id; }
    @Override public boolean equals(Object o) { return o instanceof CommandeProduitPK p && Objects.equals(commandeId,p.commandeId)&&Objects.equals(produitId,p.produitId); }
    @Override public int hashCode() { return Objects.hash(commandeId,produitId); }
}
