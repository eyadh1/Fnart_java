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

    public boolean resetPassword(String email, String newPassword) {
        String query = "UPDATE user SET password = ? WHERE email = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            // Hash the new password
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            
            statement.setString(1, hashedPassword);
            statement.setString(2, email);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

/*
    public boolean verifyPassword(String email, String passwordToVerify) {
        String query = "SELECT password FROM user WHERE email = ?";

        try {
            PreparedStatement stmt = cnx.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                return BCrypt.checkpw(passwordToVerify, storedHash);
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }
*/
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



    public User login(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // 1. First check if account is active
                String status = resultSet.getString("status");
                if (!"ACTIVE".equals(status)) {
                    System.out.println("Account is not active");
                    return null;
                }

                // 2. Verify the password against the hashed version
                String storedHash = resultSet.getString("password");
                if (BCrypt.checkpw(password, storedHash)) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(storedHash);
                    user.setPhone(resultSet.getString("phone"));
                    user.setGender(resultSet.getString("gender"));
                    user.setStatus(status);

                    // Parse the role
                    String rolesStr = resultSet.getString("roles");
                    if (rolesStr != null && !rolesStr.isEmpty()) {
                        String roleStr = rolesStr.replace("[", "").replace("]", "").replace("\"", "");
                        user.setRole(convertToRole(roleStr));
                    }

                    return user;
                } else {
                    System.out.println("Password doesn't match");
                }
            } else {
                System.out.println("No user found with this email");
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



    public boolean createUser(User user) {
        String query = "INSERT INTO users (name, email, password, phone, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getPhone());
            
            // If the role is ADMIN, THERAPIST, or ARTIST, set status to PENDING
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.THERAPIST || user.getRole() == Role.ARTIST) {
                statement.setString(5, user.getRole().name());
                statement.setString(6, "PENDING");
            } else {
                statement.setString(5, user.getRole().name());
                statement.setString(6, "ACTIVE");
            }

            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getPendingUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE status = 'PENDING'";
        
        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                users.add(createUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean approveUser(int userId) {
        String query = "UPDATE users SET status = 'ACTIVE' WHERE id = ?";
        
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean rejectUser(int userId) {
        String query = "DELETE FROM users WHERE id = ? AND status = 'PENDING'";
        
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ?";
        
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(createUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public int getTotalUsersCount() {
        String query = "SELECT COUNT(*) FROM users";
        
        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPendingUsersCount() {
        String query = "SELECT COUNT(*) FROM users WHERE status = 'PENDING'";
        
        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getActiveUsersCount() {
        String query = "SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'";
        
        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setNom(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setPhone(resultSet.getString("phone"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        user.setStatus(resultSet.getString("status"));
        return user;
    }
}
