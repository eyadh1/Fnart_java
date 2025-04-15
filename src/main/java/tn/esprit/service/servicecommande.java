package tn.esprit.service;

import tn.esprit.models.commande;
import tn.esprit.models.artwork;
import tn.esprit.interfaces.IService;
import tn.esprit.utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class servicecommande implements IService<commande> {
    private Connection conn;
    
    public servicecommande() {
        conn = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(commande commande) throws SQLException {
        String qry = "INSERT INTO `commande` (`nom`, `adress`, `telephone`, `email`, `artwork_id`, `date`, `totale`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstm = conn.prepareStatement(qry);
        pstm.setString(1, commande.getNom());
        pstm.setString(2, commande.getAdress());
        pstm.setString(3, commande.getTelephone());
        pstm.setString(4, commande.getEmail());
        pstm.setInt(5, commande.getArtwork_id());
        pstm.setDate(6, new java.sql.Date(commande.getDate().getTime()));
        pstm.setDouble(7, commande.getTotale());

        pstm.executeUpdate();
    }

    @Override
    public List<commande> getAll() {
        List<commande> commandes = new ArrayList<>();
        String qry = "SELECT c.*, a.* FROM `commande` c " +
                    "LEFT JOIN `artwork` a ON c.artwork_id = a.id";

        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                // Create artwork object
                artwork artwork = new artwork();
                artwork.setId(rs.getInt("a.id"));
                artwork.setTitre(rs.getString("a.titre"));
                artwork.setDescription(rs.getString("a.description"));
                artwork.setPrix(rs.getInt("a.prix"));
                artwork.setImage(rs.getString("a.image"));
                artwork.setArtistenom(rs.getString("a.artistenom"));
                artwork.setStatus(rs.getString("a.status"));

                // Create commande object with artwork
                commande commande = new commande();
                commande.setId(rs.getInt("c.id"));
                commande.setNom(rs.getString("c.nom"));
                commande.setAdress(rs.getString("c.adress"));
                commande.setTelephone(rs.getString("c.telephone"));
                commande.setEmail(rs.getString("c.email"));
                commande.setArtwork_id(rs.getInt("c.artwork_id"));
                commande.setDate(rs.getDate("c.date"));


                commande.setArtwork(artwork);

                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll : " + e.getMessage());
        }

        return commandes;
    }

    @Override
    public void update(commande commande) {
        String qry = "UPDATE `commande` SET `nom`=?, `adress`=?, `telephone`=?, `email`=?, `artwork_id`=?, `date`=?, `totale`=? WHERE `id`=?";
        try {
            PreparedStatement pstm = conn.prepareStatement(qry);
            pstm.setString(1, commande.getNom());
            pstm.setString(2, commande.getAdress());
            pstm.setString(3, commande.getTelephone());
            pstm.setString(4, commande.getEmail());
            pstm.setInt(5, commande.getArtwork_id());
            pstm.setDate(6, new java.sql.Date(commande.getDate().getTime()));
            pstm.setDouble(7, commande.getTotale());
            // Ajouté
            pstm.setInt(9, commande.getId());

            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(commande commande) {
        String qry = "DELETE FROM `commande` WHERE `id`=?";
        try {
            PreparedStatement pstm = conn.prepareStatement(qry);
            pstm.setInt(1, commande.getId());
            int affectedRows = pstm.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("La suppression a échoué, aucune ligne affectée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Additional method to get a single commande with its artwork
    public commande getById(int id) {
        String qry = "SELECT c.*, a.* FROM `commande` c " +
                    "LEFT JOIN `artwork` a ON c.artwork_id = a.id " +
                    "WHERE c.id = ?";
        
        try {
            PreparedStatement pstm = conn.prepareStatement(qry);
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                // Create artwork object
                artwork artwork = new artwork();
                artwork.setId(rs.getInt("a.id"));
                artwork.setTitre(rs.getString("a.titre"));
                artwork.setDescription(rs.getString("a.description"));
                artwork.setPrix(rs.getInt("a.prix"));
                artwork.setImage(rs.getString("a.image"));
                artwork.setArtistenom(rs.getString("a.artistenom"));
                artwork.setStatus(rs.getString("a.status"));

                // Create commande object with artwork
                commande commande = new commande();
                commande.setId(rs.getInt("c.id"));
                commande.setNom(rs.getString("c.nom"));
                commande.setAdress(rs.getString("c.adress"));
                commande.setTelephone(rs.getString("c.telephone"));
                commande.setEmail(rs.getString("c.email"));
                commande.setArtwork_id(rs.getInt("c.artwork_id"));
                commande.setDate(rs.getDate("c.date"));
                commande.setTotale(rs.getDouble("c.totale"));

                commande.setArtwork(artwork);

                return commande;
            }
        } catch (SQLException e) {
            System.out.println("Erreur getById : " + e.getMessage());
        }
        
        return null;
    }
}
