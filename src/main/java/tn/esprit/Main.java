package tn.esprit;
import tn.esprit.service.serviceartwork;
import tn.esprit.models.artwork;
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

            service.add(new artwork("mmm", "beau", 150, "", "mounir", "approved"));

        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }
    }
}