app.directive("alertInfo", function() {
    return {
        restrict: 'E',
        scope: {
            info: '='
        },
        templateUrl: 'js/templates/{{ info.type }}.html'
    }
});