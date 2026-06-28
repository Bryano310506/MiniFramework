package main.java.core;

import java.lang.reflect.Method;

public class MethodTarget {
    private Class<?> clazz;
    private Method method;

    // getters and setters
    public Class<?> getClazz() {
        return clazz;
    }
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }
    
}
