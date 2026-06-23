package main.java.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.annotation.Controller;
import main.java.annotation.UrlMapping;
import main.java.utils.ClassManager;
import main.java.utils.MethodManager;

public class FrontControllerServlet extends HttpServlet {
    List<String> listClasses;
    List<Method> listMethods;

    public void init() throws ServletException {
        String initial = this.getInitParameter("Controller");
        try {
            listClasses = ClassManager.intoString(ClassManager.loadClasses(initial, Controller.class));
            listMethods = MethodManager.getAllMethods(initial);
        } catch(Exception e) {
            e.printStackTrace();
            listClasses = null;
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        processRequest(req, res);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        res.setContentType("text/plain;charset=UTF-8");
        String uri = req.getRequestURI();
        PrintWriter out = res.getWriter();

        printClasses(uri, out);
        out.println();

        List<Method> showListFindMethods = new ArrayList<>();
        List<Method> showListAllMethods = new ArrayList<>();
        showListFindMethods = MethodManager.filterMethodWithEndPoint(listMethods, uri, UrlMapping.class);
        showListAllMethods = MethodManager.filterMethodWithAnnotation(listMethods, UrlMapping.class);

        if(showListFindMethods.isEmpty()) {
            out.println("Aucun Method est associé à cette endpoint");
            out.println();
        } else {
            Map<String, List<String>> mapTrouver = MethodManager.getInformationMethod(showListFindMethods);
            out.println("L'information des methods associé à cette endpoint");
            printMethods(mapTrouver, out);
            out.println("===============================================");
        }

        out.println("Liste des methods existant avec l'annotation et ses informations");
        Map<String, List<String>> map = MethodManager.getInformationMethod(showListAllMethods);
        printMethods(map, out);

    }

    // private void filterRequest(String uri) {
    //     if(uri.endsWith(".html") || uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".jsp")) {

    //     }
    // }

    private void printClasses(String uri, PrintWriter out) {
        out.println("Liste des classes :");
        for(String s : this.listClasses) {
            out.println("\t" + s);
        }
    }

    private void printMethods(Map<String, List<String>> map, PrintWriter out) {
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String clazz = entry.getKey();
            List<String> methods = entry.getValue();

            out.println("Classe : " + clazz);
            for (String m : methods) {
                out.println("\t-> " + m + "()");
            }
            out.println();
        }
        out.flush();
    }
    
}