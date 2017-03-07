app.controller('mainController', ['$scope', function ($scope, $window) {
    //main page content controller properties or functions
    $scope.drop = function (id) {
        //alert(id);
        $('.dropdownwrap' + id).slideToggle();
    };

    $scope.drill = function (id) {
        $('.drilldownwrap' + id).slideToggle();
    }

    $scope.plot = function (info) {
        //data1 is priceData
        if (!info.plotted) {
            alert(info.AnomalyID);
            var data1 = [12, 1, 3, 17, 6, 3, 7]
            var dates = [];
            dates[0] = new Date(1488412195 * 1000); //should have startTime*1000 here

            for (i = 1; i < 7; i++) {
                dates[i] = new Date(dates[i - 1].getTime + (30 * 1000));
            }
            var times = [];
            for (i = 0; i < 7; i++) {
                if (dates[i].getMinutes() < 10) {
                    times[i] = dates[i].getHours() + ":0" + dates[i].getMinutes() + dates[i].getSeconds();
                } else {
                    times[i] = dates[i].getHours() + ":" + dates[i].getMinutes() + dates[i].getSeconds();
                }
            }

            var Average = {
                x: times,
                y: data1,
                type: 'scatter',
                name: 'Price Average'
            };
            var layout = {
                xaxis: {
                    title: 'Time',
                    titlefont: {
                        family: 'Courier New, monospace',
                        size: 18,
                        color: '#7f7f7f'
                    }
                },
                yaxis: {
                    title: 'Price',
                    titlefont: {
                        family: 'Courier New, monospace',
                        size: 18,
                        color: '#7f7f7f'
                    }
                },
                margin: {
                    l: 40,
                    r: 40,
                    b: 40,
                    t: 40
                },
                legend: {
                    xanchor: "center",
                    yanchor: "top",
                    y: -0.3, // play with it
                    x: 0.5 // play with it
                }
            };

            var data = [Average];
            Plotly.newPlot('pumpdump' + info.AnomalyID, data, layout);
            info.plotted = true;
        } else {
            alert("already plotted: " + info.AnomalyID);
        }
    };

    //    $.ready(function() {

    //  });
}]);