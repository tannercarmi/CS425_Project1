/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.project1.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;

/**
 *
 * @author tanto
 */
public class UsersDAO {
    
    private final DAOFactory daoFactory;
    private final String QUERY_FIND_USER = "SELECT * FROM user WHERE `key`=?";
    private final String QUERY_FIND_ACCESS = "SELECT * FROM user_access WHERE userid=? AND access_date=?";
    private final String QUERY_CREATE_ACCESS = "INSERT INTO user_access (userid, access_date, access_count) VALUES (?, ?, ?)";
    private final String QUERY_UPDATE_ACCESS = "UPDATE user_access SET access_count = access_count + 1 WHERE userid=? AND access_date=?";
    
    UsersDAO(DAOFactory dao) {
        this.daoFactory = dao;
    }
    
    public int getAccessCount(String key) {

        JSONObject json = new JSONObject();
        json.put("success", false);

        Connection conn = daoFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = conn.prepareStatement(QUERY_FIND_USER);
            ps.setString(1, key);
            
            boolean hasresults = ps.execute();

            if (hasresults) {               
                rs = ps.getResultSet();
                
                if (rs.next()) {
                    return this.findAccess(rs.getInt("id"));
                }

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

        return -1;

    }
    
    private int findAccess(int userid) {      
        String access_date = getCurrentDate();
        
        JSONObject json = new JSONObject();
        json.put("success", false);

        Connection conn = daoFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = conn.prepareStatement(QUERY_FIND_ACCESS);
            ps.setInt(1, userid);
            ps.setString(2, access_date);
            
            boolean hasresults = ps.execute();

            if (hasresults) {
                
                rs = ps.getResultSet();
                if (rs.next()) {                    
                    if (this.updateAccess(userid, access_date) != -1) {
                        return rs.getInt("access_count") + 1;
                    }
                }
                else {
                    return this.createAccess(userid, access_date);
                }
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

        return -1;

    }
    
    private int createAccess(int userid, String access_date) {

        JSONObject json = new JSONObject();
        json.put("success", false);
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                
                ps = conn.prepareStatement(QUERY_CREATE_ACCESS, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1, userid);
                ps.setString(2, access_date);
                ps.setInt(3, 1);
                
                int updateCount = ps.executeUpdate();
                boolean hasResults = updateCount > 0;
                if (hasResults) {  
                    ResultSet keys = ps.getGeneratedKeys();
                    if (keys.next()) {                      
                        return 1;
                    }
                }
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        
        finally {
            
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
            
        }
        
        return -1;
    }
    
    private int updateAccess(int userid, String access_date) {
        
        JSONObject json = new JSONObject();
        json.put("success", false);
        
        PreparedStatement ps = null;
        
        try {
            
            Connection conn = daoFactory.getConnection();
            
            if (conn.isValid(0)) {
                
                ps = conn.prepareStatement(QUERY_UPDATE_ACCESS);
                ps.setInt(1, userid);
                ps.setString(2, access_date);
                
                int updateCount = ps.executeUpdate();
                boolean hasResults = updateCount > 0;
                
                if (hasResults) {
                    return 1;
                }
                
            }
            
        }
        
        catch (Exception e) { e.printStackTrace(); }
        
        finally {

            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
            
        }
        
        return -1;
    }
    
    private String getCurrentDate() {
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dateObj.format(formatter);
        
        return date;
    }
}
