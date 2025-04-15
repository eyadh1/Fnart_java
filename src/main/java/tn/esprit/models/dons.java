package tn.esprit.models;


import java.math.BigDecimal;

public class dons {
    private Long id; // Correspond au champ "id" de type int(11), clé primaire
    private Long beneficiaireId; // Correspond au champ "beneficiaire_id" de type int(11), index
    private String type; // Correspond au champ "type" de type varchar(255)
    private BigDecimal valeur; // Correspond au champ "valeur" de type decimal(10,2)
    private String description; // Correspond au champ "description" de type longtext


    public dons(Long id, Long beneficiaireId, String type, BigDecimal valeur, String description) {
        this.id = id;
        this.beneficiaireId = beneficiaireId;
        this.type = type;
        this.valeur = valeur;
        this.description = description;
    }

    public dons(Long beneficiaireId, String type, BigDecimal valeur, String description) {
        this.beneficiaireId = beneficiaireId;
        this.type = type;
        this.valeur = valeur;
        this.description = description;
    }

    public dons() {
    }

    public int getId() {
        return Math.toIntExact(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBeneficiaireId() {
        return Math.toIntExact(beneficiaireId);
    }

    public void setBeneficiaireId(Long beneficiaireId) {
        this.beneficiaireId = beneficiaireId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "dons{" +
                "id=" + id +
                ", beneficiaireId=" + beneficiaireId +
                ", type='" + type + '\'' +
                ", valeur=" + valeur +
                ", description='" + description + '\'' +
                '}';
    }
}
