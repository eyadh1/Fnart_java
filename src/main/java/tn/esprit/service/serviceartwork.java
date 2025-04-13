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
        return List.of();
    }

    @Override
    public void update(artwork artwork) {

    }

    @Override
    public void delete(artwork artwork) {

    }
}
