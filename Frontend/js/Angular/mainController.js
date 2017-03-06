app.controller('mainController', ['$scope', function($scope) {
    //main page content controller properties or functions
    $scope.drop = function(id) {
        $(document).ready(function(e) {
            $('#dropdown' + id).on('click', function() { // !!! replace number with {{alertId}} etc.
                $('.dropdownwrap' + id).slideToggle();
            });
        });
    };
}]);