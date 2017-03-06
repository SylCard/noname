app.controller('mainController', ['$scope', function($scope) {
    //main page content controller properties or functions
    $scope.drop = function(id) {
        console.log(id);
        $('#dropdown' + id).on('click', function() { // !!! replace number with {{alertId}} etc.
            $('.dropdownwrap' + id).slideToggle();
        });
    };
}]);