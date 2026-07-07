package main.java.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.core.GlobalConfig;
import main.java.core.MethodTarget;
import main.java.exception.UrlMappingException;
import main.java.utils.MethodManager;

public class FrontControllerServlet extends HttpServlet {
    GlobalConfig globalConfig;

    public void init() throws ServletException {
        globalConfig = (GlobalConfig) getServletContext().getAttribute("globalConfig");
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

        // filtre des requetes
        filterRequest(req, res, uri);

        Map<String, MethodTarget> showListFind = new HashMap<>();
        Map<String, MethodTarget> showListAll = new HashMap<>();
        Map<String, List<String>> map = new HashMap<>();

        // recuperation des methodes
        try {
            showListAll = MethodManager.filterMethodWithAnnotation(globalConfig.getListMethods());
            showListFind = MethodManager.filterMethodWithEndPoint(globalConfig.getListMethods(), uri);
        } catch (UrlMappingException e) {
            out.println(e.getMessage());
        }

        // affichage
        if(showListFind.isEmpty()) {
            out.println("Aucun Method est associé à cette endpoint");
            out.println("\n");
            out.println("Voici les Listes des methods existant avec l'annotation et ses informations");
            map = MethodManager.getInformationMethod(showListAll);
            printMethods(map, out);

        } else {
            map = MethodManager.getInformationMethod(showListFind);
            out.println("L'information des methods associé à cette endpoint");
            printMethods(map, out);
            out.println("===============================================");
            
            // execution du methode associee
            out.println("Execution du method associee");
            showListFind.forEach((cle, methodTarget) -> {
                MethodManager.executeMethod(methodTarget); 
            });

            out.println("===============================================");

        }
    }

    private void filterRequest(HttpServletRequest req, HttpServletResponse res, String uri) 
            throws ServletException, IOException {
        if(uri.endsWith(".jsp")) {
            try {
                RequestDispatcher dispat = req.getRequestDispatcher(uri);
                dispat.forward(req, res);
            } catch (ServletException | IOException e) {
                throw e;
            }
        }
    }

    // private void printClasses(String uri, PrintWriter out) {
    //     out.println(uri);
    //     out.println("Liste des classes :");
    //     for(String s : this.listClasses) {
    //         out.println("\t" + s);
    //     }
    // }

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