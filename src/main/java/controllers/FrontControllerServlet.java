package main.java.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.annotation.WebAPI;
import main.java.core.GlobalConfig;
import main.java.core.MethodTarget;
import main.java.core.ModelAndView;
import main.java.exception.UrlMappingException;
import main.java.utils.MethodManager;
import main.java.utils.ViewManager;

public class FrontControllerServlet extends HttpServlet {
    GlobalConfig globalConfig;
    String suffix;
    String prefix;

    public void init() throws ServletException {
        globalConfig = (GlobalConfig) getServletContext().getAttribute("globalConfig");
        this.prefix = getInitParameter("prefix");
        this.suffix = getInitParameter("suffix");
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
            String methodHttp = req.getMethod();
            showListAll = MethodManager.filterMethodWithAnnotationAndHttpMethod(globalConfig.getListMethods(), methodHttp);
            showListFind = MethodManager.filterMethodWithEndPoint(globalConfig.getListMethods(), methodHttp, uri);
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
            List<MethodTarget> targets = new ArrayList<>(showListFind.values());
            MethodTarget methodTarget = targets.get(0);
            Method m = methodTarget.getMethod();

            try {       
                Object result = MethodManager.executeMethod(methodTarget); 
                
                if (result instanceof ModelAndView) {
                    ModelAndView mv = (ModelAndView) result;
                    
                    this.flush(req, res, mv);
                } else if(m.isAnnotationPresent(WebAPI.class)) {
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    
                    Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                        .create();
                        
                    String json = gson.toJson(result);
                    res.getWriter().write(json);
                } else {
                    map = MethodManager.getInformationMethod(showListFind);
                    out.println("L'URL a été trouvée, mais la méthode n'a pas retourné un ModelAndView.");
                    out.println("Type retourné : " + (result != null ? result.getClass().getName() : "void"));
                    out.println("Misy annotation webapi : " + m.isAnnotationPresent(WebAPI.class));
                    out.println("===============================================");
                    printMethods(map, out);
                }
            } catch (Exception e) {
                out.println("Erreur lors de l'exécution : " + e.getMessage());
                e.printStackTrace(out);
            }
        }
    }

    private void flush(HttpServletRequest req, HttpServletResponse res, ModelAndView mv) 
            throws ServletException, IOException{
        String page = mv.getView();
        Map<String, Object> map = mv.getAttributes();
        for(Map.Entry<String, Object> e : map.entrySet()) {
            String attributName = e.getKey();
            Object attributeValue = e.getValue();
            req.setAttribute(attributName, attributeValue);
        }
        try {
            RequestDispatcher dispat = req.getRequestDispatcher(ViewManager.createViewPath(prefix, suffix, page));
            dispat.forward(req, res); 
        } catch(ServletException | IOException e) {
            throw e;
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