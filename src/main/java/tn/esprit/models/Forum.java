package tn.esprit.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Forum {
    private int id;
    private Date date_f;
    private String titre_f;
    private User user; // Jointure avec User (composition)
    private String categorie_f;
    private String description_f;
    private String image_f;

    // Constructeur par défaut
    public Forum() {
        this.user = new User(); // Initialisation par défaut
    }

    // Constructeur avec ID seul
    public Forum(int id) {
        this.id = id;
        this.user = new User(); // Initialisation par défaut
    }

    // Constructeur pour création sans ID (insertion BD)
    public Forum(Date date_f, String titre_f, User user, String categorie_f,
                 String description_f, String image_f) {
        this.date_f = date_f;
        this.titre_f = titre_f;
        this.user = user;
        this.categorie_f = categorie_f;
        this.description_f = description_f;
        this.image_f = image_f;
    }

    // Constructeur complet avec ID
    public Forum(int id, Date date_f, String titre_f, User user,
                 String categorie_f, String description_f, String image_f) {
        this.id = id;
        this.date_f = date_f;
        this.titre_f = titre_f;
        this.user = user;
        this.categorie_f = categorie_f;
        this.description_f = description_f;
        this.image_f = image_f;
    }

    // Constructeur simplifié for ajouterForumAction  pour ajouter forum

    public Forum(Date date_f, String titre_f, String categorie_f, String description_f, String image_f) {
        this.date_f = date_f;
        this.titre_f = titre_f;
        this.categorie_f = categorie_f;
        this.description_f = description_f;
        this.image_f = image_f;
    }
    // Constructeur simplifié pour tests
    public Forum(String titre_f, String categorie_f, String description_f) {
        this.titre_f = titre_f;
        this.categorie_f = categorie_f;
        this.description_f = description_f;
        this.user = new User(); // Initialisation par défaut
        this.date_f = new Date(); // Date courante
    }

    public Forum(int id, String titreF, String descriptionF) {
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate_f() {
        return date_f;
    }

    public void setDate_f(Date date_f) {
        this.date_f = date_f;
    }

    public String getTitre_f() {
        return titre_f;
    }

    public void setTitre_f(String titre_f) {
        this.titre_f = titre_f;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.user = user;
    }

    public String getCategorie_f() {
        return categorie_f;
    }

    public void setCategorie_f(String categorie_f) {
        this.categorie_f = categorie_f;
    }

    public String getDescription_f() {
        return description_f;
    }

    public void setDescription_f(String description_f) {
        this.description_f = description_f;
    }

    public String getImage_f() {
        return image_f;
    }

    public void setImage_f(String image_f) {
        this.image_f = image_f;
    }

    // Méthode utilitaire pour la date formatée
    public String getFormattedDate() {
        if (date_f != null) {
            return new SimpleDateFormat("dd/MM/yyyy").format(date_f);
        }
        return "";
    }

    // Méthodes pour affichage dans TableView
    public String getTitle() {
        return titre_f;
    }

    public String getDescription() {
        return description_f;
    }

    public String getCategory() {
        return categorie_f;
    }

    public String getAuthorName() {
        return user != null ? user.getNom() : "Unknown";
    }

    // Equals & HashCode basés sur l'ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Forum forum = (Forum) o;
        return id == forum.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Forum{" +
                "id=" + id +
                ", date_f=" + getFormattedDate() +
                ", titre_f='" + titre_f + '\'' +
                ", user=" + (user != null ? user.getNom() + " (ID:" + user.getId() + ")" : "null") +
                ", categorie_f='" + categorie_f + '\'' +
                ", description_f='" + (description_f != null ? description_f.substring(0, Math.min(description_f.length(), 30)) + "..." : "null") +
                ", image_f='" + (image_f != null ? "[" + image_f.length() + " chars]" : "null") +
                '}';
    }
}