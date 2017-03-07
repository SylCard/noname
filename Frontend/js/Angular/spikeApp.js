var app = angular.module("spikeApp", []);

app.run(function($rootScope) {
    //array used store alerts and generate alert boxes
    $rootScope.anomalies = [{
            AnomalyID: 11,
            type: 'VolumeSpike',
            symbol: 'SYM',
            yaxis1: [12, 1, 3, 17, 6, 3, 7],
            yaxis2: [0, 0, 0, 0],
            timeBegin: 1488593766466,
            periodLen: 30000
        },
        {
            AnomalyID: 12,
            type: 'FatFinger',
            stock: {
                symbol: 'SYM',
                buyer: 'silver@pumpanddump.me',
                price: 450,
                currency: 'GBP',
                time: 1488404319,
                sector: 'Services'
            },
            severity: 50,
            RMA: 1000
        },
        {
            AnomalyID: 13,
            type: 'PumpAndDump',
            symbol: 'SYM',
            yaxisPrice: [12, 1, 3, 17, 6, 3, 7],
            timeBegin: 1488593766466,
            periodLen: 30000
        }
    ];

    //possible functions to call when alerts come in/need to be deleted aafter sending response
    $rootScope.addAnomaly = function(data) {
        $rootScope.anomalies.push(data);
    };
    $rootScope.removeAnomaly = function(index) {
        $rootScope.anomalies.splice(index, 1);
    };

    /*
    $rootScope.drop = function(id) {
        alert(id);
        $('.dropdownwrap' + id).slideToggle();
    };*/
});