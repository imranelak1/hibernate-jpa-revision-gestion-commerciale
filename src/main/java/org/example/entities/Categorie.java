package org.example.entities;
import javax.persistence.*;
import java.util.*;

@Entity @Table(name="categories")
public class Categorie {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=100) private String nom;
    @OneToMany(mappedBy="categorie",cascade=CascadeType.ALL,orphanRemoval=true) private List<Produit> produits=new ArrayList<>();
    public Categorie() {}
    public Categorie(String nom) { this.nom=nom; }
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom=nom; }
    public List<Produit> getProduits() { return produits; }
    public void addProduit(Produit produit) { produits.add(produit);produit.setCategorie(this); }
    public void removeProduit(Produit produit) { produits.remove(produit);produit.setCategorie(null); }
    @Override public String toString() { return "Categorie{id="+id+", nom='"+nom+"'}"; }
}
