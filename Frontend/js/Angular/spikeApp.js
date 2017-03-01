var app = angular.module("spikeApp", []);

app.run(function($rootScope) {
    $rootScope.anomalies = [
        {
            AnomalyID: 1,
            type: 'FatFinger',
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
});