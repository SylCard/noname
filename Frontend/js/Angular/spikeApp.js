var app = angular.module("spikeApp", []);

app.run(function($rootScope) {
    //array used store alerts and generate alert boxes
    $rootScope.anomalies = [];

    //possible functions to call when alerts come in/need to be deleted aafter sending response
    $rootScope.addAnomaly = function(data) {
        //$rootScope.anomalies.push(data);
		$rootScope.anomalies[data.AnomalyID] = data;
    };
    $rootScope.removeAnomaly = function(index) {
        //$rootScope.anomalies.splice(index, 1);
		$rootScope.anomalies[index] = null;
	};
    $rootScope.refresh = function(data) {
        $rootScope.anomalies = data;
    };

    /*
    $rootScope.drop = function(id) {
        alert(id);
        $('.dropdownwrap' + id).slideToggle();
    };*/
});
