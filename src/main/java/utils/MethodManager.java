package main.java.utils;

import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.annotation.UrlMapping;

public class MethodManager {

    public static Map<String, List<String>> getInformationMethod(List<Method> listMethod) {
        Map<String, List<String>> map = new HashMap<>();

        for (Method m : listMethod) {
            Class<?> c = m.getDeclaringClass();
            
            String className = c.getName(); 
            String methodName = m.getName();

            if (!map.containsKey(className)) {
                List<String> list = new ArrayList<>();
                list.add(methodName);
                map.put(className, list);
            } else {
                List<String> list = map.get(className);
                list.add(methodName);
            }
        }

        return map;
    }

    public static List<Method> filterMethodWithEndPoint(List<Method> listMethod, String endPoint, Class<? extends Annotation> annotation) {
        List<Method> newListMethod = filterMethodWithAnnotation(listMethod, UrlMapping.class);
        List<Method> newList = new ArrayList<>();

        for(Method m : newListMethod) {
            if (m.isAnnotationPresent(annotation)) {
                try {
                    Annotation ann = m.getAnnotation(annotation);
                    Method valueMethod = ann.annotationType().getMethod("value");
                    String value = (String) valueMethod.invoke(ann);
                    if (endPoint.endsWith(value)) {
                        newList.add(m);
                    }
                } catch (Exception e) {
                    // System.err.println("L'annotation n'a pas de méthode value() : " + e.getMessage());
                }
            }
        }

        return newList;
    }

    public static List<Method> filterMethodWithAnnotation(List<Method> listMethod, Class<? extends Annotation> annotation) {
        List<Method> newListMethod = new ArrayList<>();

        for(Method m : listMethod) {
            if(m.isAnnotationPresent(annotation)) {
                newListMethod.add(m);
            }
        }

        return newListMethod;
    }

    public static List<Method> getAllMethods(String packageName)
            throws Exception {
        List<Method> listMethod = new ArrayList<>();
        List<Class<?>> listClass = ClassManager.getClasses(packageName);

        for(Class<?> c : listClass) {
            Method[] tabMethods = c.getDeclaredMethods();
            listMethod.addAll(Arrays.asList(tabMethods));
        }

        return listMethod;      
    }

}
