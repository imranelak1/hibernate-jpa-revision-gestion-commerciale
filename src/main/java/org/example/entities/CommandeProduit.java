package org.example.entities;
import javax.persistence.*;

@Entity @Table(name="commande_produit")
public class CommandeProduit {
    @EmbeddedId private CommandeProduitPK id=new CommandeProduitPK();
    @ManyToOne(fetch=FetchType.LAZY) @MapsId("commandeId") @JoinColumn(name="commande_id") private Commande commande;
    @ManyToOne(fetch=FetchType.LAZY) @MapsId("produitId") @JoinColumn(name="produit_id") private Produit produit;
    @Column(nullable=false) private int quantite;
    public CommandeProduit() {}
    public CommandeProduit(Commande commande,Produit produit,int quantite) { if(quantite<=0)throw new IllegalArgumentException("Quantite positive requise");this.commande=commande;this.produit=produit;this.quantite=quantite; }
    public CommandeProduitPK getId() { return id; }
    public Commande getCommande() { return commande; }
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit=produit; }
    public int getQuantite() { return quantite; }
}
