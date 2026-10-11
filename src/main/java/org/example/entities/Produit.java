package org.example.entities;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name="produits")
@NamedQueries({
    @NamedQuery(name="Produit.findAll",query="select p from Produit p order by p.id"),
    @NamedQuery(name="Produit.findByCategorie",query="select p from Produit p join fetch p.categorie where p.categorie.id=:catId order by p.id"),
    @NamedQuery(name="Produit.searchByLibelle",query="select p from Produit p where lower(p.libelle) like lower(concat('%',:term,'%')) order by p.id")
})
public class Produit {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=150) private String libelle;
    @Column(nullable=false,precision=10,scale=2) private BigDecimal prix;
    @Column(name="date_creation") private LocalDate dateCreation;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="categorie_id",nullable=false) private Categorie categorie;
    public Produit() {}
    public Produit(String libelle,BigDecimal prix,Categorie categorie) { this.libelle=libelle;setPrix(prix);this.categorie=categorie;this.dateCreation=LocalDate.now(java.time.ZoneId.of("Africa/Casablanca")); }
    public Long getId() { return id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle=libelle; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { if(prix==null||prix.signum()<0)throw new IllegalArgumentException("Prix invalide");this.prix=prix; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation=dateCreation; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie=categorie; }
    @Override public String toString() { return id+" "+libelle+" "+prix+" DH"; }
}
