package main.java.utils;

import java.io.IOException;
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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import main.java.annotation.UrlMapping;
import main.java.core.FileTarget;
import main.java.core.HttpMethod;
import main.java.core.MethodTarget;
import main.java.core.MySession;
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

    public static Object[] getAllArgsValue(MethodTarget mt, Map<Parameter, ? extends List<?>> map) 
            throws IllegalAccessException, InvocationTargetException, InstantiationException, 
                    IllegalArgumentException, NoSuchMethodException, SecurityException {
        Parameter[] params = mt.getMethod().getParameters();
        Object[] args = new Object[params.length]; 

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Class<?> paramType = param.getType();

            List<?> values = map.get(param);

            if (values == null || values.isEmpty()) {
                args[i] = Utilitaire.valeurParDefaut(paramType);
                continue;
            }

            if (values.get(0) instanceof FileTarget) {
                args[i] = values.get(0);
                continue;
            }

            if (values.get(0) instanceof MySession) {
                args[i] = values.get(0);
                continue;
            }

            String firstValue = (String) values.get(0);
            String[] names = firstValue.split("::");

            if (values.size() == 1 && names.length == 1) {
                args[i] = Utilitaire.conversionType(param, firstValue);
                continue;
            }

            else {
                Object objectInstance = paramType.getDeclaredConstructor().newInstance();
                
                Map<String, String> beanMap = new HashMap<>();
                for (Object value : values) {
                    String item = (String) value;
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
    public static Map<Parameter, List<?>> matchingParameter(MethodTarget mt, HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        Map<Parameter, List<?>> result = new HashMap<>();

        for (Parameter p : mt.getMethod().getParameters()) {
            List<Object> listValeur = new ArrayList<>();

            if (p.getType() == MySession.class) {
                listValeur.add(toMySession(request));
            }
            
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

    public static Map<Parameter, List<?>> matchingMultipartParameter(MethodTarget mt, HttpServletRequest request)
            throws IOException, ServletException {
        Map<Parameter, List<?>> result = new HashMap<>();

        for (Parameter parameter : mt.getMethod().getParameters()) {
            List<Object> values = new ArrayList<>();
            String parameterName = parameter.getName();

            if (parameter.getType() == FileTarget.class) {
                Part part = request.getPart(parameterName);
                if (part != null && part.getSubmittedFileName() != null
                        && !part.getSubmittedFileName().isBlank()) {
                    values.add(toFileTarget(part));
                }
            } 
            else if (parameter.getType() == MySession.class) {
                values.add(toMySession(request));
            }
            else if (Utilitaire.valeurParDefaut(parameter.getType()) != null) {
                String[] parameterValues = request.getParameterValues(parameterName);
                if (parameterValues != null) {
                    values.addAll(Arrays.asList(parameterValues));
                }
            } else {
                for (Part part : request.getParts()) {
                    String partName = part.getName();
                    if (partName.startsWith(parameterName + ".") && part.getSubmittedFileName() == null) {
                        String attributeName = partName.substring(parameterName.length() + 1);
                        values.add(attributeName + "::" + request.getParameter(partName));
                    }
                }
            }
            result.put(parameter, values);
        }
        return result;
    }

    private static FileTarget toFileTarget(Part part) throws IOException {
        String submittedFileName = part.getSubmittedFileName();
        String fileName = submittedFileName.substring(Math.max(
                submittedFileName.lastIndexOf('/'), submittedFileName.lastIndexOf('\\')) + 1);
        int extensionIndex = fileName.lastIndexOf('.');

        FileTarget file = new FileTarget();
        file.setNom(extensionIndex > 0 ? fileName.substring(0, extensionIndex) : fileName);
        file.setExtension(extensionIndex > 0 ? fileName.substring(extensionIndex + 1) : "");
        file.setFichiers(part.getInputStream().readAllBytes());
        return file;
    }

    private static MySession toMySession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return new MySession(session);
    }

}
