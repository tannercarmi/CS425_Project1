/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.project1;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.jsu.mcis.project1.dao.CurrencyDAO;
import edu.jsu.mcis.project1.dao.DAOFactory;
import javax.servlet.ServletContext;

/**
 *
 * @author tanto
 */
public class CurrenciesServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        DAOFactory daoFactory = null;
        ServletContext context = request.getServletContext();

        if (context.getAttribute("daoFactory") == null) {
            System.err.println("*** Creating new DAOFactory ...");
            daoFactory = new DAOFactory();
            context.setAttribute("daoFactory", daoFactory);
        } 
        else {
            daoFactory = (DAOFactory) context.getAttribute("daoFactory");
        }

        response.setContentType("application/json; charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            CurrencyDAO dao = daoFactory.getCurrencyDAO();

            out.println(dao.find());

        } 
        
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
