package org.example.entities;
import javax.persistence.*;
import java.time.LocalDate;

@Entity @Table(name="clients")
public class Client extends User {
    @Column(name="date_inscription") private LocalDate dateInscription;
    public Client() {}
    public Client(String nom,LocalDate date) { setNom(nom);dateInscription=date; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate date) { dateInscription=date; }
}
