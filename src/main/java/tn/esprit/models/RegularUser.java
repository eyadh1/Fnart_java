package tn.esprit.models;

import tn.esprit.enumerations.Role;

import java.util.Date;

public class RegularUser extends User {
    private static RegularUser instance;

    public RegularUser() {
        super();
        this.setRole(Role.REGULARUSER);
    }

    public RegularUser(String nom, String email, String password, String phone, String gender, Date DateOfBirth) {
        super(nom, email, password, phone, gender,DateOfBirth);
        this.setRole(Role.REGULARUSER);
    }

    public static RegularUser getInstance() {
        if (instance == null) {
            instance = new RegularUser();
        }
        return instance;
    }

    public static RegularUser getInstance(String nom, String email, String password, String phone, String gender,  Date DateOfBirth) {
        if (instance == null) {
            instance = new RegularUser(nom, email, password, phone, gender, DateOfBirth);
        }
        return instance;
    }
} 