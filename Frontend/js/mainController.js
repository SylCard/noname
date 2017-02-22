app.controller('mainController', ['$scope', function($scope) {
    //main page content controller properties or functions
    $scope.anomalies = [];

    $scope.addAnomaly = function(data) {
        $scope.anomalies.push(data);
    };
    $scope.removeAnomaly = function(index) {
        $scope.anomalies.splice(index, 1);
    };
}]);