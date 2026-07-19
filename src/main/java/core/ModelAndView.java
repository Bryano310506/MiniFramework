package main.java.core;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private String view;
    private Map<String, Object> attributes = new HashMap<>();

    // getters and setters
    public String getView() {
        return view;
    }
    public void setView(String view) {
        this.view = view;
    }
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    public void setAttribute(Map<String, Object> attribute) {
        this.attributes = attribute;
    }

    public void addAttribute(String ref, Object objectReferencier) {
        attributes.put(ref, objectReferencier);
    }
    
}
