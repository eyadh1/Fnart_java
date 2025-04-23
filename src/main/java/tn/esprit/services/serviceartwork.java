package tn.esprit.services;
import tn.esprit.models.Artwork;
import tn.esprit.interfaces.IService;
import tn.esprit.utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceartwork implements IService<Artwork> {
    private Connection conn;
    public serviceartwork() {
        conn = MyDataBase.getInstance().getCnx();
    }
    @Override
    public void add(Artwork artwork) throws SQLException {
        String qry = "INSERT INTO `artwork`(`titre`, `prix`, `description`, `image`, `artistenom`) VALUES (?,?,?,?,?)";
        PreparedStatement pstm = conn.prepareStatement(qry);
        pstm.setString(1, artwork.getTitre());
        pstm.setInt(2, artwork.getPrix());
        pstm.setString(3, artwork.getDescription());
        pstm.setString(4, artwork.getImage());
        pstm.setString(5, artwork.getArtistenom());
        pstm.executeUpdate();
    }

    @Override
    public List<Artwork> getAll() {
        List<Artwork> artworks = new ArrayList<>();
        String qry = "SELECT * FROM `artwork`";

        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                Artwork artwork = new Artwork();
                artwork.setId(rs.getInt(1));
                artwork.setTitre(rs.getString("titre"));
                artwork.setPrix(rs.getInt("prix"));
                artwork.setDescription(rs.getString("description"));
                artwork.setImage(rs.getString("image"));
                artwork.setArtistenom(rs.getString("artistenom"));
                artworks.add(artwork);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll : " + e.getMessage());
        }

        return artworks;
    }

    @Override
    public void update(Artwork artwork) {
        String qry = "UPDATE `artwork` SET `titre`=?, `prix`=?, `description`=?, `image`=?, `artistenom`=? WHERE `id`=?";
        try {
            PreparedStatement pstm = conn.prepareStatement(qry);
            pstm.setString(1, artwork.getTitre());
            pstm.setInt(2, artwork.getPrix());
            pstm.setString(3, artwork.getDescription());
            pstm.setString(4, artwork.getImage());
            pstm.setString(5, artwork.getArtistenom());
            pstm.setInt(6, artwork.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Artwork artwork) {
        String qry = "DELETE FROM `artwork` WHERE id=?";
        try {
            PreparedStatement pstm = conn.prepareStatement(qry);
            pstm.setInt(1, artwork.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void approveArtwork(int id) throws SQLException {
        String query = "UPDATE artwork SET approved = true WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public void disapproveArtwork(int id) throws SQLException {
        String query = "UPDATE artwork SET approved = false WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public List<Artwork> getApprovedArtworks() throws SQLException {
        List<Artwork> artworks = new ArrayList<>();
        String query = "SELECT * FROM artwork WHERE approved = true";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Artwork a = new Artwork(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getInt("prix"),
                    rs.getString("image"),
                    rs.getString("artistenom"),
                    rs.getString("status"),
                    rs.getBoolean("approved")
                );
                artworks.add(a);
            }
        }
        return artworks;
    }
}

