package tn.esprit.services;

import tn.esprit.models.CommentaireF;
import tn.esprit.models.User;
import tn.esprit.models.Forum;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommentaire_f {

    private Connection conn;

    public ServiceCommentaire_f() {
        this.conn = MyDataBase.getInstance().getCnx(); // Correct initialization using MyDataBase
    }

    // **Add a Comment**
    public void ajouter(CommentaireF commentaire) {
        String query = "INSERT INTO commentaire_f (id_user_id, id_forum_id, date_c, texte_c) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, commentaire.getUser().getId());
            pst.setInt(2, commentaire.getForum().getId());
            pst.setTimestamp(3, new Timestamp(commentaire.getDate_c().getTime())); // Convert Date to SQL Timestamp
            pst.setString(4, commentaire.getTexte_c());

            pst.executeUpdate();
            System.out.println("Comment added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding comment: " + e.getMessage());
        }
    }

    // **Modify a Comment**
    public void modifier(CommentaireF commentaire) {
        String query = "UPDATE commentaire_f SET texte_c = ?, date_c = ? WHERE id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, commentaire.getTexte_c());
            pst.setTimestamp(2, new Timestamp(commentaire.getDate_c().getTime())); // Update timestamp
            pst.setInt(3, commentaire.getId());

            pst.executeUpdate();
            System.out.println("Comment modified successfully!");
        } catch (SQLException e) {
            System.err.println("Error modifying comment: " + e.getMessage());
        }
    }

    // **Delete a Comment**
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM commentaire_f WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Comment deleted successfully!");
        } catch (SQLException e) {
            // Propagate the SQLException to the caller
            throw new SQLException("Error deleting comment with id " + id + ": " + e.getMessage(), e);
        }
    }

    // **Retrieve all Comments**
    public List<CommentaireF> afficher() {
        List<CommentaireF> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaire_f";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                CommentaireF commentaire = new CommentaireF(
                        rs.getInt("id"),
                        this.getUserById(rs.getInt("id_user_id")),  // Fetch user by ID
                        this.getForumById(rs.getInt("id_forum_id")), // Fetch forum by ID
                        rs.getTimestamp("date_c"),
                        rs.getString("texte_c")
                );
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching comments: " + e.getMessage());
        }
        return commentaires;
    }

    // **Retrieve all Comments using getAll()**
    public List<CommentaireF> getAll() {
        return afficher(); // Reusing the afficher() method
    }

    // **Retrieve Comments by Forum ID**
    public List<CommentaireF> getCommentairesByForumId(int forumId) {
        List<CommentaireF> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaire_f WHERE id_forum_id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, forumId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                CommentaireF commentaire = new CommentaireF(
                        rs.getInt("id"),
                        this.getUserById(rs.getInt("id_user_id")),  // Fetch user by ID
                        this.getForumById(rs.getInt("id_forum_id")), // Fetch forum by ID
                        rs.getTimestamp("date_c"),
                        rs.getString("texte_c")
                );
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching comments by forum ID: " + e.getMessage());
        }
        return commentaires;
    }

    // **Helper method to get User by ID**
    private User getUserById(int userId) {
        User user = null;
        String query = "SELECT * FROM user WHERE id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setEmail(rs.getString("email"));
                // Set other user properties as needed
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return user != null ? user : new User(userId); // Fall back to simple ID-only User if not found
    }

    // **Helper method to get Forum by ID**
    private Forum getForumById(int forumId) {
        Forum forum = null;
        String query = "SELECT * FROM forum WHERE id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, forumId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                forum = new Forum(
                        rs.getInt("id"),
                        rs.getDate("date_f"),
                        rs.getString("titre_f"),
                        new User(rs.getInt("id_user_id")),
                        rs.getString("categorie_f"),
                        rs.getString("description_f"),
                        rs.getString("image_f")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching forum: " + e.getMessage());
        }
        return forum != null ? forum : new Forum(forumId); // Fall back to simple ID-only Forum if not found
    }
}