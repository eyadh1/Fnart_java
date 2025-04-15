package tn.esprit.service;
import tn.esprit.models.artwork;
import tn.esprit.interfaces.IService;
import tn.esprit.utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceartwork implements IService<artwork> {
    private Connection conn;
    public serviceartwork() {
        conn = MyDataBase.getInstance().getCnx();
    }
    @Override
    public void add(artwork artwork) throws SQLException {
        //todo's
        //creation req sql
        //execution
        String qry = "INSERT INTO `artwork`( `titre`, `prix`, `description`, `image`, `artistenom`,`status`) VALUES (?,?,?,?,?,?)";
        PreparedStatement pstm = conn.prepareStatement(qry);
        pstm.setString(1, artwork.getTitre());
        pstm.setInt(2, artwork.getPrix());
        pstm.setString(3, artwork.getDescription());
        pstm.setString(4, artwork.getImage());
        pstm.setString(5, artwork.getArtistenom());
        pstm.setString(6, artwork.getStatus());
        pstm.executeUpdate();
    }

    @Override
    public List<artwork> getAll() {
        List<artwork> artworks = new ArrayList<>();
        String qry = "SELECT * FROM `artwork`";

        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                artwork artwork = new artwork();
                artwork.setId(rs.getInt(1)); // correction ici
                artwork.setTitre(rs.getString("titre"));
                artwork.setPrix(rs.getInt("prix"));
                artwork.setDescription(rs.getString("description"));
                artwork.setImage(rs.getString("image"));
                artwork.setArtistenom(rs.getString("artistenom"));
                artwork.setStatus(rs.getString("status"));

                artworks.add(artwork); // il manquait cette ligne
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll : " + e.getMessage());
        }

        return artworks; // retourne la vraie liste
    }

    @Override
    public void update(artwork artwork) {
        String qry = "UPDATE `artwork` SET `titre`=?, `prix`=?, `description`=?, `image`=?, `artistenom`=?, `status`=? WHERE `id`=?";
        try {
            PreparedStatement pstm = conn.prepareStatement(qry);
            pstm.setString(1, artwork.getTitre());
            pstm.setInt(2, artwork.getPrix());
            pstm.setString(3, artwork.getDescription());
            pstm.setString(4, artwork.getImage());
            pstm.setString(5, artwork.getArtistenom());
            pstm.setString(6, artwork.getStatus());
            pstm.setInt(7, artwork.getId()); // <-- correction ici
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }




    @Override
    public void delete(artwork artwork) {
        String qry = "DELETE FROM `artwork` WHERE `id`=?";
        try {
            PreparedStatement pstm = conn.prepareStatement(qry);
            pstm.setInt(1, artwork.getId());
            int affectedRows = pstm.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("La suppression a échoué, aucune ligne affectée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}

