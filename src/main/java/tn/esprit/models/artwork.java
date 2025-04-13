package tn.esprit.models;

public class artwork {
    private int id;
    private String titre;
    private String description;
    private int prix;
    private String image;
    private String artistenom;
    private String status;
    public artwork() {
        super();
    }
    public artwork(int id, String titre, String description, int prix, String image, String artistenom, String status) {
        super();
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.image = image;
        this.artistenom = artistenom;
        this.status = status;

    }
    public artwork(String titre, String description, int prix, String image, String artistenom, String status) {
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.image = image;
        this.artistenom = artistenom;
        this.status = status;
    }

    public int getId(int id) {
        return id;
    }

    public void setId(int id) {
        this.id = id;

    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getArtistenom() {
        return artistenom;
    }

    public void setArtistenom(String artistenom) {
        this.artistenom = artistenom;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "artwork{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", image='" + image + '\'' +
                ", artistenom='" + artistenom + '\'' +
                ", status='" + status + '\'' +
                "}/n";
    }


}
