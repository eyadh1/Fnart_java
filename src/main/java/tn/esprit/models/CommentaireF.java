package tn.esprit.models;

import java.util.Date;

public class CommentaireF {
    private int id;
    private User user; // Association with the User class
    private Forum forum; // Association with the Forum class
    private Date date_c;
    private String texte_c;

    // Default constructor
    public CommentaireF() {
    }

    // Constructor with all fields
    public CommentaireF(int id, User user, Forum forum, Date date_c, String texte_c) {
        this.id = id;
        this.user = user;
        this.forum = forum;
        this.date_c = date_c;
        this.texte_c = texte_c;
    }

    // Constructor with userId instead of User object
    public CommentaireF(int id, int userId, Forum forum, Date date_c, String texte_c) {
        this.id = id;
        this.user = new User(userId);  // Create User with just the ID
        this.forum = forum;
        this.date_c = date_c;
        this.texte_c = texte_c;
    }

    // Constructor without id (for inserts)
    public CommentaireF(User user, Forum forum, Date date_c, String texte_c) {
        this.user = user;
        this.forum = forum;
        this.date_c = date_c;
        this.texte_c = texte_c;
    }

    // Constructor with userId and forum (for inserts)
    public CommentaireF(int userId, Forum forum, Date date_c, String texte_c) {
        this.user = new User(userId);  // Create User with just the ID
        this.forum = forum;
        this.date_c = date_c;
        this.texte_c = texte_c;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public Date getDate_c() {
        return date_c;
    }

    public void setDate_c(Date date_c) {
        this.date_c = date_c;
    }

    public String getTexte_c() {
        return texte_c;
    }

    public void setTexte_c(String texte_c) {
        this.texte_c = texte_c;
    }

    @Override
    public String toString() {
        return "Commentaire_f{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : "null") +
                ", forum=" + (forum != null ? forum.getTitre_f() : "null") +
                ", date_c=" + date_c +
                ", texte_c='" + texte_c + '\'' +
                '}';
    }
}