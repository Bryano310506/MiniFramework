package main.java.utils;

public class ViewManager {
    
    public static String createViewPath(String prefix, String suffix, String page) {
        StringBuilder str = new StringBuilder();
        str.append(prefix).append(page).append(suffix);
        return str.toString();
    }

}
