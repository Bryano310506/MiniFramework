package main.java.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import main.java.annotation.Controller;
import main.java.core.GlobalConfig;
import main.java.utils.ClassManager;
import main.java.utils.MethodManager;

public class StartupListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        GlobalConfig globalConfig = new GlobalConfig();

        List<String> listClasses = new ArrayList<>();
        List<Method> listMethods = new ArrayList<>();
        String initial = context.getInitParameter("Controller");

        try {
            listClasses = ClassManager.intoString(ClassManager.loadClasses(initial, Controller.class));
            listMethods = MethodManager.getAllMethods(initial);
        } catch(Exception e) {
            e.printStackTrace();
            listClasses = null;
        }

        globalConfig.setListClasses(listClasses);
        globalConfig.setListMethods(listMethods);

        context.setAttribute("globalConfig", globalConfig);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }

}
