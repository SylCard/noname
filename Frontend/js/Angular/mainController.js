app.controller('mainController', ['$scope', function($scope) {
    //main page content controller properties or functions
    $scope.drop = function(id) {
        alert(id);
        $('.dropdownwrap' + id).slideToggle();
    };
}]);