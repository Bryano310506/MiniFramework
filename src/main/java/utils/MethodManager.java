package main.java.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils2.BeanUtils;

import main.java.annotation.UrlMapping;
import main.java.core.HttpMethod;
import main.java.core.MethodTarget;
import main.java.core.RouteKey;
import main.java.exception.UrlMappingException;

public class MethodManager {

    public static Object executeMethod(MethodTarget mt, Object[] args) 
            throws Exception {
        Object instance = mt.getClazz().getConstructor().newInstance();
        return mt.getMethod().invoke(instance, args);
    }

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

    public static Map<String, MethodTarget> filterMethodWithEndPoint(List<Method> listMethod, String methodHttp, String endPoint) 
            throws UrlMappingException {
        Map<String, MethodTarget> endpointMap = filterMethodWithAnnotationAndHttpMethod(listMethod, methodHttp);
        Map<String, MethodTarget> endpointTrouver = new HashMap<>();

        for (String registeredUrl : endpointMap.keySet()) {
            if (endPoint.endsWith(registeredUrl)) {
                endpointTrouver.put(registeredUrl, endpointMap.get(registeredUrl));
            }
        }

        return endpointTrouver;
    }

    public static Map<String, MethodTarget> filterMethodWithAnnotationAndHttpMethod(List<Method> listMethod, String methodHttp) 
            throws UrlMappingException {
        Map<String, MethodTarget> endpointMap = new HashMap<>();
        Set<RouteKey> routeUnique = new HashSet<>();

        for (Method m : listMethod) {
            if (m.isAnnotationPresent(UrlMapping.class)) {
                UrlMapping annotation = m.getAnnotation(UrlMapping.class);
                String endpoint = annotation.value();
                HttpMethod httpMethod = annotation.method();

                MethodTarget target = new MethodTarget();
                target.setClazz(m.getDeclaringClass()); 
                target.setMethod(m);

                RouteKey routeKey = new RouteKey(endpoint, httpMethod);

                if (!methodHttp.equals(httpMethod.name())) {
                    continue;
                }

                if (!routeUnique.add(routeKey)) {
                    throw new UrlMappingException("Un doublon a été détecté !! Veuillez le corriger");
                }

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

    public static Object[] getAllArgsValue(MethodTarget mt, Map<Parameter, List<String>> map) 
            throws IllegalAccessException, InvocationTargetException, InstantiationException, 
                    IllegalArgumentException, NoSuchMethodException, SecurityException {
        Parameter[] params = mt.getMethod().getParameters();
        Object[] args = new Object[params.length]; 

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Class<?> paramType = param.getType();

            List<String> values = map.get(param);

            if (values == null || values.isEmpty()) {
                args[i] = Utilitaire.valeurParDefaut(paramType);
                continue;
            }

            String[] names = values.get(0).split("::");

            if (values.size() == 1 && names.length == 1) {
                args[i] = Utilitaire.conversionType(param, values.get(0));
                continue;
            }

            else {
                Object objectInstance = paramType.getDeclaredConstructor().newInstance();
                
                Map<String, String> beanMap = new HashMap<>();
                for (String item : values) {
                    if (item != null && item.contains("::")) {
                        String[] parts = item.split("::", 2); // Découpe en max 2 parties
                        String attrName = parts[0];
                        String attrValue = (parts.length > 1) ? parts[1] : "";
                        
                        beanMap.put(attrName, attrValue);
                    }
                }

                if (!beanMap.isEmpty()) {
                    BeanUtils.populate(objectInstance, beanMap);
                }
                args[i] = objectInstance;
            }
        }
        return args;
    }

    // contrainte nom attribut input == nom parametre
    public static Map<Parameter, List<String>> matchingParameter(MethodTarget mt, Map<String, String[]> params) {
        Map<Parameter, List<String>> result = new HashMap<>();

        for (Parameter p : mt.getMethod().getParameters()) {
            List<String> listValeur = new ArrayList<>();

            if (Utilitaire.valeurParDefaut(p.getType()) != null) {
                String[] values = params.get(p.getName());
                if (values != null) {
                    listValeur.addAll(Arrays.asList(values));
                }
                result.put(p, listValeur);
                continue;
            }

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String[] names = entry.getKey().split("\\.");
                if (names.length == 2 && names[0].equals(p.getName())) {
                    for (String s : entry.getValue()) {
                        listValeur.add(names[1] + "::" + s);
                    }
                }
            }
            result.put(p, listValeur);
        }
        return result;
    }

}
