<%-- 
    Document   : index
    Created on : Nov 9, 2022, 7:25:03 PM
    Author     : tanto
--%>

<!DOCTYPE html>
<html>

<head>
    <title>Lab #4</title>
    <meta charset="utf-8">
    <script type="text/javascript" src="Scripts/script.js"></script>
    <script type="text/javascript" src="Scripts/jquery-3.6.1.min.js"></script>
</head>
    <body>
        
        <form name="calculatorform" id="calculatorform">

        <fieldset>

            <legend>Currency Conversion Calculator</legend>

            <p>Convert to: <span id="currencylist"></span></p>

            <p>
                <label for="usd_value">Value (in USD):</label>
                <input type="number" name="usd_value" id="usd_value">

                <label for="rate_date">Date:</label>
                <input type="date" name="rate_date" id="rate_date">

                <label for="apikey">API Key:</label>
                <input name="apikey" id="apikey">
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
