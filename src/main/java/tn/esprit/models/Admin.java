package tn.esprit.models;

import tn.esprit.enumerations.Role;

import java.util.Date;

public class Admin extends User {
    private static Admin instance;

    public Admin() {
        super();
        this.setRole(Role.ADMIN);
    }

    public Admin(String nom, String email, String password, String phone, String gender, Date DateOfBirth) {
        super(nom, email, password, phone, gender, DateOfBirth);
        this.setRole(Role.ADMIN);
    }

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    public static Admin getInstance(String nom, String email, String password, String phone, String gender, Date DateOfBirth) {
        if (instance == null) {
            instance = new Admin(nom, email, password, phone, gender,  DateOfBirth);
        }
        return instance;
    }
} 
