var app = angular.module("spikeApp", []);

app.run(function($rootScope) {
    //array used store alerts and generate alert boxes
    $rootScope.anomalies = [
        {
            AnomalyID: 1,
            type: 'FatFinger',  //needed to identify which alert template to use when generating html
            stock: {
                symbol: 'SYM',
                buyer: 'silver@pumpanddump.me',
                price: 450,
                currency: 'GBP',
                time: 1488404319
            },
            severity: 50,
            RMA: 1000
        }
    ];

    //possible functions to call when alerts come in/need to be deleted aafter sending response
    $rootScope.addAnomaly = function(data) {
        $rootScope.anomalies.push(data);
    };
    $rootScope.removeAnomaly = function(index) {
        $rootScope.anomalies.splice(index, 1);
    };
});