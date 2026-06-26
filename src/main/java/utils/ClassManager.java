package main.java.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassManager {

    public static List<String> intoString(List<Class<?>> listClass) {
        List<String> listString = new ArrayList<>();

        for(Class<?> c : listClass) {
            listString.add(c.getSimpleName());
        }

        return listString;
    }
    
    public static List<Class<?>> loadClasses(String packageName, Class<? extends Annotation> annotation) 
            throws Exception {
        List<Class<?>> list = new ArrayList<>();

        list = findClassWithAnnotation(packageName, annotation);

        return list;
    }

    public static List<Class<?>> findClassWithAnnotation(String packageName, Class<? extends Annotation> annotation)
            throws Exception {
        List<Class<?>> listClass = getClasses(packageName);
        List<Class<?>> annotatedClasses = new ArrayList<>();

            for(Class<?> c : listClass) {
                if(c.isAnnotationPresent(annotation)) {
                    annotatedClasses.add(c);
                }
            }
            
        return annotatedClasses;
    }

    public static List<Class<?>> getClasses(String packageName)
            throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL resource = classLoader.getResource(path);
        
        List<Class<?>> classes = new ArrayList<>();
        if (resource == null) return classes;

        File directory = new File(resource.getFile());
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }

}
