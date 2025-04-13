package tn.esprit.services;

import tn.esprit.enumerations.Role;
import tn.esprit.interfaces.IService;
import tn.esprit.models.User;
import tn.esprit.utils.MyDataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    private Connection cnx;

    public UserService(){
        cnx = MyDataBase.getInstance().getCnx();
    }

    public boolean signUp(User user) {
        String checkEmailQuery = "SELECT COUNT(*) FROM user WHERE email = ?";
        String insertQuery = "INSERT INTO user (nom, email, password, roles, phone, gender) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Check if email already exists
            PreparedStatement checkStmt = cnx.prepareStatement(checkEmailQuery);
            checkStmt.setString(1, user.getEmail());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Email already exists");
                return false;
            }

            // Hash the password
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            // Format the role for database insertion
            String roleString = "[\"" + user.getRole().toString() + "\"]";

            // Insert the new user
            PreparedStatement insertStmt = cnx.prepareStatement(insertQuery);
            insertStmt.setString(1, user.getNom());
            insertStmt.setString(2, user.getEmail());
            insertStmt.setString(3, hashedPassword);
            insertStmt.setString(4, roleString);
            insertStmt.setString(5, user.getPhone());
            insertStmt.setString(6, user.getGender());

            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error during signup: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void add(User user) {
        String requete = "INSERT INTO user (nom, email, password, roles, phone, gender) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            // Format the role for database insertion
            String roleString = "[\"" + user.getRole().toString() + "\"]";

            PreparedStatement statement = cnx.prepareStatement(requete);
            statement.setString(1, user.getNom());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, roleString);
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getGender());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String requete = "SELECT * FROM user";
        try {
            Statement statement = cnx.createStatement();
            ResultSet resultSet = statement.executeQuery(requete);
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setNom(resultSet.getString("nom"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setPhone(resultSet.getString("phone"));
                
                // Parse the role from the database format
                String rolesStr = resultSet.getString("roles");
                if (rolesStr != null && !rolesStr.isEmpty()) {
                    // Remove the square brackets and quotes
                    String roleStr = rolesStr.replace("[", "").replace("]", "").replace("\"", "");
                    // Convert database role to enum
                    Role role = convertToRole(roleStr);
                    user.setRole(role);
                }
                
                user.setGender(resultSet.getString("gender"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    @Override
    public void update(User user) {
        String requete = "UPDATE user SET nom=?, email=?, password=?, roles=?, phone=?, gender=? WHERE id=?";
        try {
            // Format the role for database insertion
            String roleString = "[\"" + user.getRole().toString() + "\"]";

            PreparedStatement statement = cnx.prepareStatement(requete);
            statement.setString(1, user.getNom());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, roleString);
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getGender());
            statement.setInt(7, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(User user) {
        String requete = "DELETE FROM user WHERE id=?";
        try {
            PreparedStatement statement = cnx.prepareStatement(requete);
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Helper method to convert database role string to Role enum
    private Role convertToRole(String roleStr) {
        switch (roleStr) {
            case "ROLE_USER":
                return Role.REGULARUSER;
            case "ROLE_ADMIN":
                return Role.ADMIN;
            case "ROLE_ARTIST":
                return Role.ARTIST;
            case "ROLE_THERAPIST":
                return Role.THERAPIST;
            default:
                return Role.REGULARUSER; // Default role
        }
    }
}
