package tn.esprit.models;

import tn.esprit.enumerations.Role;

import java.util.Date;

public class Artist extends User {
    private static Artist instance;
    private String artStyle;



    public Artist() {
        super();
        this.setRole(Role.ARTIST);
    }

    public Artist(String nom, String email, String password, String phone, String gender, Date DateOfBirth, String artStyle) {
        super(nom, email, password, phone, gender, DateOfBirth);
        this.setRole(Role.ARTIST);
        this.artStyle = artStyle;

    }

    public static Artist getInstance() {
        if (instance == null) {
            instance = new Artist();
        }
        return instance;
    }

    public static Artist getInstance(String nom, String email, String password, String phone, String gender,Date DateOfBirth, String artStyle) {
        if (instance == null) {
            instance = new Artist(nom, email, password, phone, gender,DateOfBirth, artStyle);
        }
        return instance;
    }

    public String getArtStyle() {
        return artStyle;
    }

    public void setArtStyle(String artStyle) {
        this.artStyle = artStyle;
    }



} 