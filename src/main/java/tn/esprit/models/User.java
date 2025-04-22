package tn.esprit.models;

import java.util.Date;
import java.util.List;

public class User {
    private int id;
    private String nom;
    private String email;
    private String password;
    private List<String> roles; // ou String[] roles selon votre besoin
    private String phone;
    private Date dateofbirth; // ou LocalDate si vous utilisez Java 8+
    private Gender gender; // ou List<String> si le genre peut contenir plusieurs valeurs

    // Constructeur avec id seulement (peut être gardé pour la compatibilité)
    public User(int id) {
        this.id = id;
    }

    public User() {

    }

    public enum Gender {
        MALE, FEMALE
    }

    // Constructeur complet
    public User(int id, String nom, String email, String password, List<String> roles,
                String phone, Date dateofbirth, String gender) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.phone = phone;
        this.dateofbirth = dateofbirth;
        this.gender = Gender.valueOf(gender);
    }

    // Getters et setters pour tous les attributs
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(Date dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", roles=" + roles +
                ", phone='" + phone + '\'' +
                ", dateofbirth=" + dateofbirth +
                ", gender='" + gender + '\'' +
                '}';
    }

}