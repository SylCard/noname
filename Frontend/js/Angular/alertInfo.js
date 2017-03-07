app.directive("alertInfo", function() {
    return {
        restrict: 'E',
        scope: {
            info: '=',
            drop: '&',
            drill: '&'
        },
        template: '<div ng-include src="\'js/Angular/templates/\'+info.type+\'.html\'"></div>'
    }
});