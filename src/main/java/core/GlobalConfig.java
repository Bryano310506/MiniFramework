package main.java.core;

import java.lang.reflect.Method;
import java.util.List;

public class GlobalConfig {
    List<String> listClasses;
    List<Method> listMethods;

    // getters and setters
    
    public List<Method> getListMethods() {
        return listMethods;
    }
    public void setListMethods(List<Method> listMethods) {
        this.listMethods = listMethods;
    }
    public List<String> getListClasses() {
        return listClasses;
    }
    public void setListClasses(List<String> listClasses) {
        this.listClasses = listClasses;
    }
    
}