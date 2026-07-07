package main.java.exception;

public class UrlMappingException extends Exception {

    public UrlMappingException() {
        super("Une erreur s'est produite, verifier vos endpoints");
    }
    
    public UrlMappingException(String message) {
        super(message);
    }

}
