package tn.esprit.models;

public class beneficiaires {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private String cause;
    private String est_elle_association;
    private String description;
    private String status = "En attente";
    private Double valeur_demande;


    public beneficiaires(Long id, String nom, String email, String telephone, String cause, String estElleAssociation, String description, String status, Double valeur_demande) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.cause = cause;
        this.est_elle_association = est_elle_association;
        this.description = description;
        this.status = "En attente";
        this.valeur_demande = valeur_demande;
    }

    public beneficiaires() {
        this.nom = "";
        this.email = "";
        this.telephone = "";
        this.cause = "";
        this.est_elle_association = "Non";
        this.description = "";
        this.status = "En attente";
        this.valeur_demande = null;
    }

    public beneficiaires(String nom, String email, String telephone, String estElleAssociation, String cause, String description, Double valeurDemande) {
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.est_elle_association = estElleAssociation;
        this.cause = cause;
        this.description = description;
        this.status = "En attente";
        this.valeur_demande = valeurDemande;
    }


    public beneficiaires(String text, String text1, String text2, String value, String text3, String text4, String text5) {
    }

    public int getId() {
        return Math.toIntExact(id);
    }

    public void setId(Long id) {
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getEstElleAssociation() {
        return est_elle_association;
    }

    public void setEstElleAssociation(String estElleAssociation) {
        this.est_elle_association = est_elle_association;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getValeurDemande() {
        return valeur_demande;
    }

    public void setValeurDemande(Double valeurDemande) {
        this.valeur_demande = valeurDemande;
    }

    @Override
    public String toString() {
        return "beneficiaires{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", cause='" + cause + '\'' +
                ", estElleAssociation='" + est_elle_association + '\'' +
                ", description='" + description + '\'' +
               /* ", status='" + status + '\'' +*/
                ", valeurDemande=" + valeur_demande +
                '}';
    }
}


