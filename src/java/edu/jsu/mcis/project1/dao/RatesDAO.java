/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.project1.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author tanto
 */
public class RatesDAO {
        
        private final DAOFactory daoFactory;
        private final String QUERY_SELECT = "SELECT * FROM rate WHERE rate_date = ?";
        private final String QUERY_EXTERNAL_DB = "https://testbed.jaysnellen.com:8443/JSUExchangeRatesServer/rates";
        private final String QUERY_CREATE = "INSERT INTO rate (currencyid, rate_date, rate)"
                        + "VALUES (?,?,?)";
        private final String QUERY_SELECT_DATE_CURRENCY = "SELECT * FROM rate WHERE rate_date = ? AND currencyid = ?";

        /* Constructor */
        RatesDAO(DAOFactory daoFactory) {
                this.daoFactory = daoFactory;
        }

        /**
         * Queries the database for the rate information
         * If the information does not exist,
         * then it is fetched by an external source using updateInternalDB()
         * 
         * @param key
         * @param date - String for date of the rates
         * @return - JSON String containing rate information
         */
        public String find(String key, String date) {
            if (keyIsValid(key)) {
                if (accessLimitReached(key)) {
                    return "{\"message\"Access limit reached\",\"success\": false}";
                }

                JSONArray jsonArray = new JSONArray();
                Connection conn = daoFactory.getConnection();
                PreparedStatement ps = null;
                ResultSet rs = null;
                JSONObject json = new JSONObject();

                    try {

                            ps = conn.prepareStatement(QUERY_SELECT);
                            ps.setString(1, date);

                            boolean hasresults = ps.execute();

                            if (hasresults) {
                                        
                                rs = ps.getResultSet();
                                        
                                if (rs.next()) {
                                    // Diagnostic Print
                                    System.err.print(rs);

                                    Map results = new LinkedHashMap<String, String>();

                                    json.put("date", date);
                                    results.put(rs.getString("currencyid"), rs.getDouble("rate"));
                                        
                                    while (rs.next()) {
                                                        results.put(rs.getString("currencyid"), rs.getDouble("rate"));
                                                      }
                                    json.put("rates", results);

                                }
                                
                                // If rates for the date are not found then the DB updates from external source
                                else {
                                    updateInternalDB(date);
                                    return find(key, date);
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
                            
                return JSONValue.toJSONString(json);
                
            } 
            else {
                return "{\"message\":\"This API requires a valid access key\",\"success\": false}";
            }
        }

        public String findByDateCurrency(String key, String date, String currency) {

            if (keyIsValid(key)) {
                if (accessLimitReached(key)) {
                    return "{\"message\"Invalid Key\",\"success\": false}";
                }

                JSONArray jsonArray = new JSONArray();
                Connection conn = daoFactory.getConnection();
                PreparedStatement ps = null;
                ResultSet rs = null;
                JSONObject json = new JSONObject();

                    try {

                            ps = conn.prepareStatement(QUERY_SELECT_DATE_CURRENCY);
                            ps.setString(1, date);
                            ps.setString(2, currency);

                            boolean hasresults = ps.execute();

                            if (hasresults) {
                                rs = ps.getResultSet();
                                
                                if (rs.next()) {
                                    // Dianostic Print
                                    System.err.print(rs);

                                    Map results = new LinkedHashMap<String, String>();

                                    json.put("date", date);
                                    results.put(rs.getString("currencyid"), rs.getDouble("rate"));
                                    
                                    while (rs.next()) {
                                        results.put(rs.getString("currencyid"), rs.getDouble("rate"));
                                    }
                                    json.put("rates", results);

                                }
                                
                                // If rates for the date is not found then the DB is update from external source
                                else {
                                    updateInternalDB(date);
                                    return findByDateCurrency(key, date, currency);
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
                        
                    return JSONValue.toJSONString(json);
                } 
                else {
                    return "{\"message\"Invalid Key\",\"success\": false}";
                }
        }

        /**
         * Queries external source for additional rate information,
         * Then updates the local DB & returns this new rate data in JSON format
         * 
         * @param date - String for date of the rates
         * @return - JSON String of rate data
         */
        public Boolean updateInternalDB(String date) {

                // request from external api
                Connection conn = daoFactory.getConnection();
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                        // HTTP GET request to external api for rate data
                        String urlString = "https://testbed.jaysnellen.com:8443/JSUExchangeRatesServer/rates?date="
                                        + date;
                        URL url = new URL(urlString);

                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");

                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                        con.getInputStream()));
                        String inputLine;
                        String output = "";
                        while ((inputLine = in.readLine()) != null) {
                                System.out.println(inputLine);
                                output = inputLine;

                        }
                        in.close();

                        // Dianostic Print
                        System.out.println("output-----------" + output);

                        // Parse the output of HTTP Request to external API
                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(output);

                        // Diagnostic Print
                        Object rates = json.get("rates");
                        System.out.println(JSONValue.toJSONString(rates));

                        // Create Rate map
                        ContainerFactory containerFactory = new ContainerFactory() {
                                @Override
                                public Map createObjectContainer() {
                                        return new LinkedHashMap<>();
                                }

                                @Override
                                public List creatArrayContainer() {
                                        return new LinkedList<>();
                                }
                        };
                        Map map = (Map) parser.parse(JSONValue.toJSONString(json.get("rates")), containerFactory);

                        /*
                         * If addition to the database is successful,
                         * the find method is called again to return the newly added data
                         */
                        if (addCurrencyData(map, date)) {
                                return true;
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

                // Use find method to re-Query database that now includes the new data from
                // extern. API
                return false;
        }

        /**
         * Accepts Map of rate data and data to add to local DB
         * 
         * @param map
         * @param date
         * @return
         */
        private Boolean addCurrencyData(Map map, String date) {

                PreparedStatement ps = null;
                ResultSet rs = null;

                try {

                        Connection conn = daoFactory.getConnection();
                        ps = conn.prepareStatement(QUERY_CREATE);

                        Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                        while (itr.hasNext()) {
                                Map.Entry<String, Object> entry = itr.next();
                                System.out.println("retrieved Key = " + entry.getKey() +
                                                ", retrieved Value = " + entry.getValue());

                                ps.setString(1, entry.getKey());
                                ps.setString(2, date);
                                ps.setDouble(3, (Double) entry.getValue());
                                ps.addBatch();

                        }
                        int[] r = ps.executeBatch();
                        conn.commit();

                        int updateCount = ps.executeUpdate();

                        if (updateCount > 0) {
                                return true;
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }

                finally {

                        if (rs != null) {
                                try {
                                        rs.close();
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                        if (ps != null) {
                                try {
                                        ps.close();
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }

                }
                return false;
        }

        private Boolean keyIsValid(String key) {
                UsersDAO userDAO = daoFactory.getUsersDAO();

                if (userDAO.checkIfValidKey(key)) {
                        return true;
                } 
                else {
                        return false;
                }
        }

        private boolean accessLimitReached(String key) {
                // Unfinished
                return false;
        }
}
