angular.module('realtimeData', ['ngRoute', 'realtimeData.data'])
    .controller('DashboardCtrl', ['$scope', 'Anomalies', 'socketio', function ($scope, Anomalies, socketio) {
        'use strict';

        $scope.anomalies = Anomalies.query();

        socketio.on('anomaly', function (msg) {
            $scope.anomalies.push(msg);
        });
    }])
    .controller('CreateCtrl', ['$scope', '$location', 'Anomalies', function ($scope, $location, Anomalies) {
        'use strict';

        $scope.save = function (newAnomaly) {
            Anomalies.save(newAnomaly);
            $location.path('/');
        };


        $scope.cancel = function () {
            $location.path('/');
        };

    }])
    .config(['$routeProvider', function ($routeProvider) {
        'use strict';

        $routeProvider
            .when('/', {
                controller: 'DashboardCtrl',
                templateUrl: 'partials/dashboard.html'
            })
            .when('/new', {
                controller: 'CreateCtrl',
                templateUrl: 'partials/anomaly.html'
            })
            .otherwise({
                redirectTo: '/'
            });
    }])
    .filter('reverse', function () {
        'use strict';

        return function (items) {
            return items.slice().reverse();
        };
    })

    .factory('socketio', ['$rootScope', function ($rootScope) {
        'use strict';

        var socket = io.connect();
        return {
            on: function (eventName, callback) {
                socket.on(eventName, function () {
                    var args = arguments;
                    $rootScope.$apply(function () {
                        callback.apply(socket, args);
                    });
                });
            },
            emit: function (eventName, data, callback) {
                socket.emit(eventName, data, function () {
                    var args = arguments;
                    $rootScope.$apply(function () {
                        if (callback) {
                            callback.apply(socket, args);
                        }
                    });
                });
            }
        };
    }]);
