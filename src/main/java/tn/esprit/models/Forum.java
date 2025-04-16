package tn.esprit.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Forum {
    private int id;
    private Date date_f;
    private String titre_f;
    private User user; // Association avec la classe User
    private String categorie_f;
    private String description_f;
    private String image_f; // Ajout de l'attribut image_f pour stocker le chemin ou l'URL de l'image
    public Forum(Date date_f, String description_f, String categorie_f, String titre_f) {
        this.date_f = date_f;
        this.description_f = description_f;
        this.categorie_f = categorie_f;
        this.titre_f = titre_f;
    }

    public String getFormattedDate() {
        if (date_f != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(date_f);  // Retourner la date formatée
        }
        return "";
    }

    // Constructeur par défaut
    public Forum(int id, String titre_f, String categorie_f) {
        this.id = id;
        this.date_f = date_f;
        this.titre_f = titre_f;
        this.categorie_f = categorie_f;
        this.description_f = description_f;
        this.image_f = image_f;
        this.user = new User(1); // ID utilisateur statique
    }

    // Constructeur avec ID
    public Forum(int id) {
        this.id = id;
        this.user = new User(1); // ID utilisateur statique
    }

    // Constructeur avec tous les attributs
    public Forum(int id, Date date_f, String titre_f, User user, String categorie_f, String description_f, String image_f) {
        this.id = id;
        this.date_f = date_f;
        this.titre_f = titre_f;
        this.user = user;
        this.categorie_f = categorie_f;
        this.description_f = description_f;
        this.image_f = image_f;
    }

    // Constructeur avec ID sans User
    public Forum(int id, Date date_f, String titre_f, String categorie_f, String description_f, String image_f) {
        this.id = id;
        this.date_f = date_f;
        this.titre_f = titre_f;
        this.categorie_f = categorie_f;
        this.description_f = description_f;
        this.image_f = image_f;
        this.user = new User(1);
    }

    // Constructeur sans ID (pour insertion en BD)
    public Forum(Date date_f, String titre_f, String categorie_f, String description_f, String image_f) {
        this.date_f = date_f;
        this.titre_f = titre_f;
        this.categorie_f = categorie_f;
        this.description_f = description_f;
        this.image_f = image_f;
        this.user = new User(1);
    }

    // Getters et Setters
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

    // Méthodes supplémentaires pour liaison avec TableView
    public String getTitle() {
        return titre_f;
    }

    public String getDescription() {
        return description_f;
    }

    public String getCategory() {
        return categorie_f;
    }

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
                ", date_f=" + date_f +
                ", titre_f='" + titre_f + '\'' +
                ", user=" + (user != null ? user.getId() : "null") +
                ", categorie_f='" + categorie_f + '\'' +
                ", description_f='" + description_f + '\'' +
                ", image_f='" + image_f + '\'' +
                '}';
    }
}
