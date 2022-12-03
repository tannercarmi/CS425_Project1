var Lab4 = (function () {

    var rates = null;

    var createCurrencyMenu = function (currencies) {

        $("#currencylist").append("<select title=\"currencymenu\" id=\"currencymenu\">" + "</select name>");
        
        currencies.forEach(element => {
            $("#currencymenu").append("<option value=\"" + element['id'] + "\">" + element['id'] + " (" + element['description'] + ")");
        });
    };

    var convert = function (rate_date) {

        //log rates to console
        for (const key in rates) {
            console.log(key + "----" + rates[key]);
        }

        var usd_value = $("#usd_value").val();
        var usd_to_eur = convertToEuro(usd_value);
        var target_currency = $("#currencymenu").val().trim();
        var target_rate = rates[target_currency];
        var result;

        var result_string = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: target_currency,
        });

        result = result_string.format(target_rate * usd_to_eur);

        var usd_value_string = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        });

        $("#output").html("The equivalent of " + usd_value_string.format(usd_value) + " in " + target_currency + " for the date " + rate_date + " is: " + result);
    
    };

    var getRatesAndConvert = function (key, rate_date) {

        console.log("Getting rates for " + rate_date + " ...");

        var url = 'http://localhost:8180/CS425_Project1/RatesServlet/';

        url += rate_date;
        url += "?key=";
        url += key;

        console.log("custom url----  " + url);

        $.ajax({
            url: url,
            method: 'GET',
            dataType: 'json',
            success: function (response) {
                rates = response.rates;
                convert(rate_date);

            }
        })

    };

    function convertToEuro(usd) {
        usd_rate = rates['USD'];
        eur = usd / usd_rate;
        return eur;
    };


    return {

        getCurrenciesList: function () {

            $.ajax({
                url: 'http://localhost:8180/CS425_Project1/CurrenciesServlet',
                method: 'GET',
                dataType: 'json',
                success: function (response) {

                    createCurrencyMenu(response);

                }
            })
        },

        onClick: function () {

            var rate_date = $("#rate_date").val();
            var key = $('#apikey').val();

            if (rate_date === "") {
                alert("Please enter or select a date in the \"Date\" field!");
            }
            else {

                // if rates not retrieved yet, or if date is different, fetch new rates

                if ((rates === null) || (rate_date !== rates["date"])) {
                    getRatesAndConvert(key, rate_date);
                }

                // if rates available, perform the conversion

                else {
                    convert(rate_date);
                }

            }

        }

    };

})();