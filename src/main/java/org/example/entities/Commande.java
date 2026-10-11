package org.example.entities;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity @Table(name="commandes")
public class Commande {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="date_commande",nullable=false) private LocalDate dateCommande;
    @OneToMany(mappedBy="commande",cascade=CascadeType.ALL,orphanRemoval=true) private List<CommandeProduit> lignes=new ArrayList<>();
    public Commande() {}
    public Commande(LocalDate date) { dateCommande=date; }
    public Long getId() { return id; }
    public LocalDate getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDate date) { dateCommande=date; }
    public List<CommandeProduit> getLignes() { return lignes; }
    public void addProduit(Produit p,int quantite) {
        if(lignes.stream().anyMatch(l->Objects.equals(l.getProduit().getId(),p.getId())))throw new IllegalArgumentException("Produit deja dans la commande");
        lignes.add(new CommandeProduit(this,p,quantite));
    }
}
