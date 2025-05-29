package com.example.emsismartpresence;

public class Document {
    private String titre;
    private String description;
    private String date;
    private String type;

    // Constructeur par défaut requis pour Firestore
    public Document() {
    }

    public Document(String titre, String description, String date, String type) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    // Getters
    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    // Setters
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }
}