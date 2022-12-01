var Lab4 = (function () {

    var rates = null;

    var convert = function () {
        
        var date = rates["date"];
        
        var usd_value = Number($("#usd_value").val());
        var usd_rate = Number(rates["rates"]["USD"]);
        var target_currency = $("#target_currency").val();
        var target_rate = Number(rates["rates"][target_currency]);
        
        var usd_to_eur = usd_value / usd_rate;
        var final_result = usd_to_eur * target_rate;

        var usd_value_string = (usd_value).toLocaleString('en-US', {
            style: 'currency',
            currency: 'USD',
        });
        var result_string = (final_result).toLocaleString('en-US', {
            style: 'currency',
            currency: target_currency,
        });
        $("#output").append("<p>The equivalent of " + usd_value_string + " in " + target_currency + " for the date " + date + " is: " + result_string + "</p>");

    };

    var getRatesAndConvert = function (rate_date, key) {

        console.log("Getting rates for " + rate_date + " ...");
        
        var ajaxURL = 'http://localhost:8180/CS425_Project1/rate?date=' + rate_date + '&key=' + key;
        
        $.ajax({
            url: ajaxURL,
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                rates = data;
                convert();
            },
            error: function(xhr, statusText, response) {
                alert("Error!");
                $("output").html(response);
                
            }
        });

    };

    return {

        onClick: function () {

            var rate_date = $("#rate_date").val();
            var key = $("#access_key").val();

            if (key === "") {
                alert("You must enter a valid key!");
            }
            else {
                if (rate_date === "") {
                    alert("Please enter or select a date in the \"Date\" field!");
                }
                else {


                    if ((rates === null) || (rate_date !== rates["date"])) {
                        getRatesAndConvert(rate_date, key);
                    }


                    else {
                        convert();
                    }

                }
            }
            

        }

    };

})();