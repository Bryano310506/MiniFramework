package main.java.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        
        String url = req.getRequestURL().toString();
        // String query = req.getQueryString();
        // if (query != null) url += "?" + query;
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();

        processRequest(url, out);
        
    }

    private void processRequest(String url, PrintWriter out) {
        out.println("<html><body>");
        out.println("<p>" + url + "</p>");
        out.println("</body></html>");
    }

    // @Override
    // protected void doPost(HttpServletRequest req, HttpServletResponse res) 
    //         throws ServletException, IOException {
    
    // }

}