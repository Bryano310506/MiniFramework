package main.java.context;

import java.util.HashMap;
import java.util.Map;

/**
 * ApplicationContext
 */
public class ApplicationContext {
    private static final Map<Class<?>, Object> beanInstances = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        if (!beanInstances.containsKey(clazz)) {
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                beanInstances.put(clazz, instance);
            } catch (Exception e) {
                throw new RuntimeException("Impossible d'instancier la classe : " + clazz.getName(), e);
            }
        }
        return (T) beanInstances.get(clazz);
    }
    
}