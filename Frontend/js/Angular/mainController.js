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
        if (info.type == 'PumpAndDump') {
            $scope.plotPump(info);
        } else if (info.type == 'VolumeSpike') {
            $scope.plotVolume(info);
        } else return;
    }

    $scope.plotPump = function (info) {
        //data1 is priceData
        if (!info.plotted) {
            var dates = [];
            dates[0] = new Date(info.timeBegin);

            for (i = 1; i < info.yaxisPrice.length; i++) {
                dates[i] = new Date(dates[i - 1].getTime() + info.periodLen);
            }
            var times = [];
            for (i = 0; i < 7; i++) {
                if (dates[i].getMinutes() < 10) {
                    times[i] = dates[i].getHours() + ":0" + dates[i].getMinutes() + ":" + dates[i].getSeconds();
                } else {
                    times[i] = dates[i].getHours() + ":" + dates[i].getMinutes() + ":" + dates[i].getSeconds();
                }
            }

            var Average = {
                x: times,
                y: info.yaxisPrice,
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
        } else return;
    };

    $scope.plotVolume = function (info) {
        //alert("still no function. cant plot for: " + info.AnomalyID);
        if (!info.plotted) {
            var dates = [];
            dates[0] = new Date(info.timeBegin);
            alert("date 0 is: " + dates[0]);

            for (i = 1; i < info.yaxis1.length; i++) {
                dates[i] = new Date(dates[i - 1].getTime() + info.periodLen);
                alert("date " + i + " is: " + dates[i]);
            }
            var times = [];
            for (i = 0; i < 7; i++) {
                if (dates[i].getMinutes() < 10) {
                    times[i] = dates[i].getHours() + ":0" + dates[i].getMinutes() + ":" + dates[i].getSeconds();
                } else {
                    times[i] = dates[i].getHours() + ":" + dates[i].getMinutes() + ":" + dates[i].getSeconds();
                }
            }
            var Average = {
                x: times,
                y: info.yaxis1,
                type: 'scatter',
                name: 'Volumes Average',
                line: {
                    dash: 'dot',
                    width: 4
                }
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
                    title: 'Volume',
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
            Plotly.newPlot('volumespike' + info.AnomalyID, data, layout);
            info.plotted = true;
        } else return;
    }
}]);