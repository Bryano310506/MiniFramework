package main.java.core;

/**
 * FileTarget
 */
public class FileTarget {
    private String nom;
    private String extension;
    private byte[] fichiers;

    public FileTarget() {}

    public String getNom() {
        return nom;
    }

    public String getExtension() {
        return extension;
    }

    public byte[] getFichiers() {
        return fichiers;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    public void setFichiers(byte[] fichiers) {
        this.fichiers = fichiers;
    }
    
}