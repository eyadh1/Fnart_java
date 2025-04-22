package tn.esprit.test;

import tn.esprit.models.Commentaire_f;
import tn.esprit.models.Forum;
import  tn.esprit.services.ServiceCommentaire_f;

import java.sql.SQLException;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws SQLException {
        // Création d'une date SQL
        java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());

        // Création d'un service forum
        // ServiceForum sp = new ServiceForum();



        //sp.ajouter(forum);
        // sp.supprimer(41);
        // Affichage de tous les forums
        //  System.out.println(sp.getAll());
        ServiceCommentaire_f serviceCommentaire = new ServiceCommentaire_f();
        Forum forum = new Forum(51);
        Commentaire_f commentaire = new Commentaire_f(1, forum, sqlDate,  "Ceci est un commentaire test.");
        Commentaire_f modif = new Commentaire_f(22,1, forum, sqlDate,  "modif");
        //serviceCommentaire.ajouter(commentaire);
        System.out.println(serviceCommentaire.getAll());
        // serviceCommentaire.supprimer(21);
        serviceCommentaire.modifier(modif);
    }
}
