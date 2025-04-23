package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Forum;
import tn.esprit.models.User;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ServiceForum implements IService<Forum> {
    Connection cnx = MyDataBase.getInstance().getCnx();

    @Override
    public void ajouter(Forum forum) throws SQLException {
        // Vérifier si l'utilisateur existe
        int userId = (forum.getUser() != null) ? forum.getUser().getId() : getCurrentUserId();
        if (!userExists(userId)) {
            throw new SQLException("L'utilisateur avec l'ID " + userId + " n'existe pas. Veuillez créer un utilisateur d'abord.");
        }

        String req = "INSERT INTO forum (date_f, titre_f, id_user_id, categorie_f, description_f, image_f) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setDate(1, new java.sql.Date(forum.getDate_f().getTime()));
        ps.setString(2, forum.getTitre_f());
        ps.setInt(3, userId);
        ps.setString(4, forum.getCategorie_f());
        ps.setString(5, forum.getDescription_f());
        ps.setString(6, forum.getImage_f());
        ps.executeUpdate();
        System.out.println("Forum ajouté avec succès !");
    }

    private int getCurrentUserId() {
        // TODO: Implement proper user session management
        // For now, return a valid user ID from your database
        return 1; // Make sure this ID exists in your user table
    }

    private boolean userExists(int userId) throws SQLException {
        String req = "SELECT COUNT(*) FROM user WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    public Forum getOneByTitle(String title) throws SQLException {
        Forum forum = null;
        String req = "SELECT * FROM forum WHERE titre_f=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, title);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            int id = res.getInt("id");
            Date date_f = res.getDate("date_f");
            String titre_f = res.getString("titre_f");
            String categorie_f = res.getString("categorie_f");
            String description_f = res.getString("description_f");
            String image_f = res.getString("image_f");
            User user = new User(res.getInt("id_user_id"));
            forum = new Forum(id, date_f, titre_f, user, categorie_f, description_f, image_f);
        }
        return forum;
    }

    public Set<String> getAllTitles() throws SQLException {
        Set<String> titles = new HashSet<>();
        String req = "SELECT titre_f FROM forum";  // SQL to fetch only the titles
        Statement st = cnx.createStatement();
        ResultSet res = st.executeQuery(req);
        while (res.next()) {
            String title = res.getString("titre_f");
            titles.add(title);
        }
        return titles;
    }

    @Override
    public void modifier(Forum forum) throws SQLException {
        String req = "UPDATE forum SET date_f=?, titre_f=?, categorie_f=?, description_f=?, image_f=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setDate(1, new java.sql.Date(forum.getDate_f().getTime()));
        ps.setString(2, forum.getTitre_f());
        ps.setString(3, forum.getCategorie_f());
        ps.setString(4, forum.getDescription_f());
        ps.setString(5, forum.getImage_f());
        ps.setInt(6, forum.getId());
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Forum mis à jour avec succès !");
        } else {
            System.out.println("Aucune mise à jour effectuée pour l'ID: " + forum.getId());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM forum WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Forum supprimé avec succès !");
    }

    @Override
    public Forum getOneById(int id) throws SQLException {
        Forum forum = null;
        String req = "SELECT * FROM forum WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            int forumId = res.getInt("id");
            Date date_f = res.getDate("date_f");
            String titre_f = res.getString("titre_f");
            String categorie_f = res.getString("categorie_f");
            String description_f = res.getString("description_f");
            String image_f = res.getString("image_f");
            User user = new User(res.getInt("id_user_id")); // Get the user associated with the forum
            forum = new Forum(forumId, date_f, titre_f, user, categorie_f, description_f, image_f);
        }
        return forum;
    }

    public Set<Forum> getAll() throws SQLException {
        Set<Forum> forums = new HashSet<>();
        String sql = "SELECT id, date_f, titre_f, categorie_f, description_f, image_f FROM forum"; // Correct SQL
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        while (rs.next()) {
            Forum forum = new Forum();
            forum.setId(rs.getInt("id"));
            forum.setDate_f(rs.getDate("date_f"));
            forum.setTitre_f(rs.getString("titre_f"));
            forum.setCategorie_f(rs.getString("categorie_f"));
            forum.setDescription_f(rs.getString("description_f"));
            forum.setImage_f(rs.getString("image_f"));
            forums.add(forum);
        }
        return forums;
    }
}