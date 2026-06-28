package main.java.utils;

import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.annotation.UrlMapping;
import main.java.core.MethodTarget;

public class MethodManager {

    public static Map<String, List<String>> getInformationMethod(Map<String, MethodTarget> map) {
        Map<String, List<String>> mapList = new HashMap<>();

        for (Map.Entry<String, MethodTarget> entry : map.entrySet()) {
            String endpoint = entry.getKey();
            MethodTarget target = entry.getValue();

            List<String> infoList = new ArrayList<>();
            
            if (target != null && target.getMethod() != null) {
                infoList.add("Classe: " + target.getClazz().getSimpleName());
                infoList.add("Méthode: " + target.getMethod().getName());
                
                if (target.getMethod().isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping annotation = target.getMethod().getAnnotation(UrlMapping.class);
                    infoList.add("HTTP: " + annotation.method());
                }
            }

            mapList.put(endpoint, infoList);
        }

        return mapList;
    }

    public static Map<String, MethodTarget> filterMethodWithEndPoint(List<Method> listMethod, String endPoint) {
        Map<String, MethodTarget> endpointMap = filterMethodWithAnnotation(listMethod);
        Map<String, MethodTarget> endpointTrouver = new HashMap<>();

        for (String registeredUrl : endpointMap.keySet()) {
            if (endPoint.endsWith(registeredUrl)) {
                endpointTrouver.put(registeredUrl, endpointMap.get(registeredUrl));
            }
        }

        return endpointTrouver;
    }

    public static Map<String, MethodTarget> filterMethodWithAnnotation(List<Method> listMethod) {
        Map<String, MethodTarget> endpointMap = new HashMap<>();

        for (Method m : listMethod) {
            if (m.isAnnotationPresent(UrlMapping.class)) {
                UrlMapping annotation = m.getAnnotation(UrlMapping.class);
                String endpoint = annotation.value();

                MethodTarget target = new MethodTarget();
                target.setClazz(m.getDeclaringClass()); 
                target.setMethod(m);

                endpointMap.put(endpoint, target);
            }
        }

        return endpointMap;
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
