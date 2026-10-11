package org.example.entities;
import javax.persistence.*;

@Entity @Table(name="users") @Inheritance(strategy=InheritanceType.JOINED)
public abstract class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=100) private String nom;
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom=nom; }
}
