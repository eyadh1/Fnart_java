package tn.esprit;
import tn.esprit.services.serviceartwork;
import tn.esprit.models.Artwork;
import tn.esprit.utils.MyDataBase;
import java.sql.Connection;
import java.sql.SQLException;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Connection conn = MyDataBase.getInstance().getCnx();
        Connection connection1 = MyDataBase.getInstance().getCnx();

        System.out.println(conn);
        System.out.println(connection1);

        serviceartwork service = new serviceartwork();
        try {
            Artwork artwork = new Artwork("mmm", "beau", 150, "", "mounir");
            artwork.setStatus("approved");
            service.add(artwork);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}