package tn.esprit.test;

import tn.esprit.enumerations.Role;
import tn.esprit.models.User;
import tn.esprit.services.UserService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();

        // Test SignUp
        System.out.println("=== Testing SignUp ===");
        User adminUser = new User();
        adminUser.setNom("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("admin123");
        adminUser.setRole(Role.ADMIN);
        adminUser.setPhone("1234567890");
        adminUser.setGender("Male");

        boolean signupResult = userService.signUp(adminUser);
        System.out.println("Admin SignUp Result: " + (signupResult ? "Success" : "Failed"));

        // Test Add
        System.out.println("\n=== Testing Add ===");
        User artistUser = new User();
        artistUser.setNom("Artist User");
        artistUser.setEmail("artist@example.com");
        artistUser.setPassword("artist123");
        artistUser.setRole(Role.ARTIST);
        artistUser.setPhone("0987654321");
        artistUser.setGender("Female");

        userService.add(artistUser);
        System.out.println("Artist added with ID: " + artistUser.getId());

        // Test GetAll
        System.out.println("\n=== Testing GetAll ===");
        List<User> users = userService.getAll();
        System.out.println("Total users: " + users.size());
        for (User user : users) {
            System.out.println("User: " + user.getNom() + 
                             " | Email: " + user.getEmail() + 
                             " | Role: " + user.getRole());
        }

        // Test Update
        System.out.println("\n=== Testing Update ===");
        artistUser.setNom("Updated Artist");
        artistUser.setRole(Role.THERAPIST);
        userService.update(artistUser);
        System.out.println("User updated: " + artistUser.getNom() + " - New Role: " + artistUser.getRole());

        // Verify update
        users = userService.getAll();
        for (User user : users) {
            if (user.getId() == artistUser.getId()) {
                System.out.println("Updated user found: " + user.getNom() + " - Role: " + user.getRole());
            }
        }

        // Test Delete
        System.out.println("\n=== Testing Delete ===");
        userService.delete(adminUser);
        System.out.println("Admin user deleted: " + adminUser.getNom());

        // Final verification
        System.out.println("\n=== Final User Count ===");
        users = userService.getAll();
        System.out.println("Remaining users: " + users.size());
        for (User user : users) {
            System.out.println("User: " + user.getNom() + " | Role: " + user.getRole());
        }
    }
} 