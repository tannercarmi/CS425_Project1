<%-- 
    Document   : index
    Created on : Nov 9, 2022, 7:25:03 PM
    Author     : tanto
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        
        <form name="calculatorform" id="calculatorform">

            <fieldset>

                <legend>Currency Conversion Calculator</legend>

                <p>Convert to: <span id="currencymenu">GBP (Pound sterling)</span></p>

                <p>
                    <label for="usd_value">Value (in USD):</label>
                    <input type="number" name="usd_value" id="usd_value">

                    <label for="rate_date">Date:</label>
                    <input type="date" name="rate_date" id="rate_date">
                </p>

                <p><input type="button" value="Convert" onclick="Lab4.onClick();"></p>

            </fieldset>

        </form>

        <div id="output"></div>

        <script type="text/javascript">
            Lab4.getCurrenciesList(); /* uncomment this in Part Two */
        </script>

    </body>
</html>
