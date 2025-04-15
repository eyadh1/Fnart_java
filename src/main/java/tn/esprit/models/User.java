package tn.esprit.models;

import tn.esprit.enumerations.Role;

import java.util.Date;

public class User {

    private int id;
    private String nom, email, password, phone, gender, status;
    private Role role;
    private Date DateOfBirth;

    public User() {
        // Default constructor
    }

    public User(String nom, String email, String password, String phone, String gender) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.status = "PENDING";
    }

    public User(String nom, String email, String password, String phone, String gender, Role role) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
        this.status = "PENDING";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", role=" + role +
                ", status='" + status + '\'' +
                '}';
    }
}
