package tn.esprit.models;

import java.util.Date;

public class Commande {
    private int id;
    private String nom;
    private String adress;
    private String telephone;
    private String email;
    private int artwork_id;
    private Date date;
    private double totale;
    private String status;
    private Artwork artwork; // import tn.esprit.models.Artwork;

    public Commande() {
        super();
    }

    public Commande(int id, String nom, String adress, String telephone, String email, Artwork artwork, Date date, double totale, String statut) {
        this.id = id;
        this.nom = nom;
        this.adress = adress;
        this.telephone = telephone;
        this.email = email;
        this.artwork = artwork;
        this.date = date;
        this.totale = totale;
        this.status = statut;
    }

    public Commande(String nom, String adress, String telephone, String email, int artwork_id, Date date, double totale, String statut) {
        this.nom = nom;
        this.adress = adress;
        this.telephone = telephone;
        this.email = email;
        this.artwork_id = artwork_id;
        this.date = date;
        this.totale = totale;
        this.status = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getArtwork_id() {
        return artwork_id;
    }

    public void setArtwork_id(int artwork_id) {
        this.artwork_id = artwork_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotale() {
        return totale;
    }

    public void setTotale(double totale) {
        this.totale = totale;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    @Override
    public String toString() {
        return "commande{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adress='" + adress + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", artwork_id=" + artwork_id +
                ", date=" + date +
                ", totale=" + totale +
                ", statut='" + status + '\'' +
                "}\n";
    }

}
