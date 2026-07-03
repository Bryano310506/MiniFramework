package main.java.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.annotation.Controller;
import main.java.core.MethodTarget;
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

        filterRequest(req, res, uri);

        PrintWriter out = res.getWriter();

        printClasses(uri, out);
        out.println();

        Map<String, MethodTarget> showListFind = new HashMap<>();
        Map<String, MethodTarget> showListAll = new HashMap<>();
        showListFind = MethodManager.filterMethodWithEndPoint(listMethods, uri);
        showListAll = MethodManager.filterMethodWithAnnotation(listMethods);

        if(showListFind.isEmpty()) {
            out.println("Aucun Method est associé à cette endpoint");
            out.println();
        } else {
            Map<String, List<String>> mapTrouver = MethodManager.getInformationMethod(showListFind);
            out.println("L'information des methods associé à cette endpoint");
            printMethods(mapTrouver, out);
            out.println("===============================================");
        }

        out.println("Liste des methods existant avec l'annotation et ses informations");
        Map<String, List<String>> map = MethodManager.getInformationMethod(showListAll);
        printMethods(map, out);

    }

    private void filterRequest(HttpServletRequest req, HttpServletResponse res, String uri) 
            throws ServletException, IOException {
        if(uri.endsWith(".html") || uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".jsp")) {
            try {
                RequestDispatcher dispat = req.getRequestDispatcher(uri);
                dispat.forward(req, res);
            } catch (ServletException | IOException e) {
                throw e;
            }
        }
    }

    private void printClasses(String uri, PrintWriter out) {
        out.println(uri);
        out.println("Liste des classes :");
        for(String s : this.listClasses) {
            out.println("\t" + s);
        }
    }

    private void printMethods(Map<String, List<String>> map, PrintWriter out) {
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String clazz = entry.getKey();
            List<String> methods = entry.getValue();

            out.println("EndPoint : " + clazz);
            for (String m : methods) {
                out.println("\t-> " + m + "()");
            }
            out.println();
        }
        out.flush();
    }
    
}