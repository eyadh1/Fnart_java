package tn.esprit.models;

public class beneficiaires {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private String cause;
    private String estElleAssociation;
    private String description;
    private String status = "En attente";
    private Double valeurDemande;


    public beneficiaires(Long id, String nom, String email, String telephone, String cause, String estElleAssociation, String description, String status, Double valeurDemande) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.cause = cause;
        this.estElleAssociation = estElleAssociation;
        this.description = description;
        this.status = "En attente";
        this.valeurDemande = valeurDemande;
    }

    public beneficiaires() {
    }

    public beneficiaires(String nom, String email, String telephone, String cause, String estElleAssociation, String description, String status, Double valeurDemande) {
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.cause = cause;
        this.estElleAssociation = estElleAssociation;
        this.description = description;
        this.status = status;
        this.valeurDemande = valeurDemande;
    }


    public beneficiaires(String text, String text1, String text2, String value, String text3, String text4, String text5) {
    }

    public Long getId() {
        return id;
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
        return estElleAssociation;
    }

    public void setEstElleAssociation(String estElleAssociation) {
        this.estElleAssociation = estElleAssociation;
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
        return valeurDemande;
    }

    public void setValeurDemande(Double valeurDemande) {
        this.valeurDemande = valeurDemande;
    }

    @Override
    public String toString() {
        return "beneficiaires{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", cause='" + cause + '\'' +
                ", estElleAssociation='" + estElleAssociation + '\'' +
                ", description='" + description + '\'' +
               /* ", status='" + status + '\'' +*/
                ", valeurDemande=" + valeurDemande +
                '}';
    }
}


