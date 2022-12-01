/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.project1.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author tanto
 */
public class RatesDAO {
    
    private final DAOFactory daoFactory;
    
    private final String QUERY_SELECT_BY_DATE = "SELECT * FROM rate WHERE rate_date=?;";
    private final String QUERY_SELECT_BY_CURRENCY = "SELECT * FROM rate WHERE rate_date=? AND currencyid=?";
    
    private final String QUERY_CREATE = "INSERT INTO rate (currencyid, rate_date, rate) VALUES (?, ?, ?)";
 
        
    RatesDAO(DAOFactory dao) {
        this.daoFactory = dao;
    }
    
    private String getCurrentDate() {
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dateObj.format(formatter);
        
        return date;
    }
    
    public String list() {       
        return this.list(getCurrentDate());
    }
    
    public String list(String date) {

        JSONObject json = new JSONObject();
        JSONObject rates = new JSONObject();

        json.put("success", false);

        Connection conn = daoFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = conn.prepareStatement(QUERY_SELECT_BY_DATE);
            ps.setString(1, date);
            boolean hasresults = ps.execute();

            if (hasresults) {
                json.put("success", hasresults);
                json.put("date", date);

                rs = ps.getResultSet();
                
                while (rs.next()) {
                    rates.put(rs.getString("currencyid"), rs.getBigDecimal("rate"));                                  
                }

                json.put("rates", rates);
                
                if (rates.isEmpty()) {
                    String result = this.create(this.getRequest(date));
                    return result;
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

        return JSONValue.toJSONString(json);

    }
    
    public String list(String currency, String date) {
        if (date == null || "".equals(date)) {
            date = getCurrentDate();
        }       
        JSONObject json = new JSONObject();
        JSONObject rates = new JSONObject();

        json.put("success", false);

        Connection conn = daoFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = conn.prepareStatement(QUERY_SELECT_BY_CURRENCY);
            ps.setString(1, date);
            ps.setString(2, currency);
            
            boolean hasresults = ps.execute();

            if (hasresults) {
                json.put("success", hasresults);
                json.put("date", date);

                rs = ps.getResultSet();
                
                if (rs.next()) {
                    rates.put(currency, rs.getString("rate"));                                  
                }
                
                               
                if (rates.isEmpty()) {
                    JSONObject temp = (JSONObject) this.getRequest(date).get("rates");
                    double tempRate = (double) temp.get(currency);
                    
                    rates.put(currency, tempRate);
                }
               
                json.put("rates", rates);
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

        return JSONValue.toJSONString(json);
    }
    
    public String create(JSONObject data) {
        String date = (String) data.get("date");
        data = (JSONObject) data.get("rates");

        JSONObject json = new JSONObject();
        json.put("success", false);
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                
                ps = conn.prepareStatement(QUERY_CREATE);
                Iterator<String> itr = data.keySet().iterator();
                while (itr.hasNext()) {
                    String currCode = itr.next();
                    String strRate = data.get(currCode).toString();
                    BigDecimal rate = new BigDecimal(strRate);
                    
                    ps.setString(1, currCode);
                    ps.setString(2, date);
                    ps.setBigDecimal(3, rate);
                    ps.addBatch();
                }
                int[] r = ps.executeBatch();
                if (r.length > 0) {
                    conn.commit();
                                  
                }
                                
                
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        
        finally {
            
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
            
        }
        
        return this.list(date);
    }
    
    private JSONObject getRequest(String rate_date) {
        String uri = "https://testbed.jaysnellen.com:8443/JSUExchangeRatesServer/rates?date=" + rate_date;
        JSONObject jsonResponse = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();            
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(in);
                String response = reader.readLine();
                JSONParser parser = new JSONParser();
                jsonResponse = (JSONObject)parser.parse(response);
            }
            connection.disconnect();
        }
        catch (Exception e) { e.printStackTrace(); }
        return jsonResponse;
    }
}
