app.controller('mainController', ['$scope', function($scope) {
    //main page content controller properties or functions

    //possible functions to call when alerts come in/need to be deleted aafter sending response
    $scope.addAnomaly = function(data) {
        $scope.anomalies.push(data);
    };
    $scope.removeAnomaly = function(index) {
        $scope.anomalies.splice(index, 1);
    };
}]);