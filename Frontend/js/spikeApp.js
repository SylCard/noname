var app = angular.module("spikeApp", [])
    .factory('socket', function(LoopBackAuth){
        //Creating connection with server
        var socket = io.connect('http://localhost:3000');
    });

app.run(function($rootScope) {
    $rootScope.anomalies = [];
});