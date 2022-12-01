/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.project1.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author tanto
 */
public class MenuDAO {
    private final DAOFactory daoFactory;
    
    private final String QUERY_SESSION_LIST = "SELECT * FROM currency ORDER BY description";
    
    MenuDAO(DAOFactory dao) {
        this.daoFactory = dao;
    }
    
    public String getCurrencyListAsHTML() {
        
        StringBuilder s = new StringBuilder();

        Connection conn = daoFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = conn.prepareStatement(QUERY_SESSION_LIST);
            
            boolean hasresults = ps.execute();

            if (hasresults) {

                rs = ps.getResultSet();
                
                s.append("<select name=\"target_currency\" id=\"target_currency\">");
                
                while (rs.next()) {
                    
                    String id = rs.getString("id");
                    String description = rs.getString("description");
                    
                    s.append("<option value=\"").append(id).append("\">");
                    s.append(description);
                    s.append("</option>");
                                        
                }
                
                s.append("</select>");

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                }
                catch (Exception e) { e.printStackTrace(); }
            }
            if (ps != null) {
                try {
                    ps.close();
                    ps = null;
                }
                catch (Exception e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                }
                catch (Exception e) { e.printStackTrace(); }
            }

        }
        
        return s.toString();
        
    }
}
