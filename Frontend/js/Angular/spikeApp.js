var app = angular.module("spikeApp", []);

app.run(function($rootScope) {
    $rootScope.anomalies = [];
});