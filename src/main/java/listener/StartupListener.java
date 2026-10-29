package main.java.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import main.java.annotation.Controller;
import main.java.context.ApplicationContext;
import main.java.core.GlobalConfig;
import main.java.utils.ClassManager;
import main.java.utils.MethodManager;
import main.java.utils.SessionManager;

public class StartupListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        GlobalConfig globalConfig = new GlobalConfig();
        SessionManager sessionManager = new SessionManager();

        List<Class<?>> loadedClassesController = new ArrayList<>();
        List<String> listClasses = new ArrayList<>();
        List<Method> listMethods = new ArrayList<>();
        String initial = context.getInitParameter("Controller");

        try {
            loadedClassesController = ClassManager.loadClasses(initial, Controller.class);
            listClasses = ClassManager.intoString(loadedClassesController);
            listMethods = MethodManager.getAllMethods(initial);

            globalConfig.setListClasses(listClasses);
            globalConfig.setListMethods(listMethods);

            instanciationAllClasses(loadedClassesController);
            
        } catch(Exception e) {
            e.printStackTrace();
            listClasses = null;
        }

        context.setAttribute("globalConfig", globalConfig);
        context.setAttribute("sessionManager", sessionManager);
    }

    private void instanciationAllClasses(List<Class<?>> loadedClasses) {
        for (Class<?> clazz : loadedClasses) {
            try {
                ApplicationContext.getBean(clazz);
            } catch (Exception e) {
                throw new RuntimeException("Impossible d'instancier la classe : " + clazz.getName(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }

}
