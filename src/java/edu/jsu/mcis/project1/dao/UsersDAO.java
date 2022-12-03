/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.project1.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author tanto
 */
public class UsersDAO {
    
    private final DAOFactory daoFactory;
        private final String QUERY_SELECT = "SELECT * FROM `user` u  WHERE u.`key` = ?;";

        UsersDAO(DAOFactory daoFactory) {
                this.daoFactory = daoFactory;
        }

        public Boolean checkIfValidKey(String key) {
                Connection conn = daoFactory.getConnection();
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {

                        ps = conn.prepareStatement(QUERY_SELECT);
                        ps.setString(1, key);
 
                        boolean hasresults = ps.execute();

                        if (hasresults) {

                                rs = ps.getResultSet();

                                if (rs.next()) {
                                        return true;
                                }

                        }

                } catch (Exception e) {
                        e.printStackTrace();
                } finally {

                        if (rs != null) {
                                try {
                                        rs.close();
                                        rs = null;
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                        if (ps != null) {
                                try {
                                        ps.close();
                                        ps = null;
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                        if (conn != null) {
                                try {
                                        conn.close();
                                        conn = null;
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }

                }
                return false;
        }

        public int getIDFromKey(String key) {

                Connection conn = daoFactory.getConnection();
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {

                        ps = conn.prepareStatement(QUERY_SELECT);
                        ps.setString(1, key);

                        boolean hasresults = ps.execute();

                        if (hasresults) {

                                rs = ps.getResultSet();

                                if (rs.next()) {
                                        return rs.getInt("id");
                                }

                        }

                } catch (Exception e) {
                        e.printStackTrace();
                } finally {

                        if (rs != null) {
                                try {
                                        rs.close();
                                        rs = null;
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                        if (ps != null) {
                                try {
                                        ps.close();
                                        ps = null;
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                        if (conn != null) {
                                try {
                                        conn.close();
                                        conn = null;
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }

                }
                return 0;
        }
}
