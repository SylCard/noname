angular.module('realtimeData.data', ['ngResource']).factory('Anomalies', ['$resource', function($resource) {
    'use strict';

    var server = $resource('/anomalies');

    return {
        save: function (newAnomaly) {
            server.save(newAnomaly);
        },

        query: function () {
            return server.query();
        }
    };
}]);
