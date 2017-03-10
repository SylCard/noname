/*
 * Get anomalies
 */
function getAnomalies(mode) {
	if (mode == 'live') {
		socket.emit('getLive');
	} else if (mode == 'historical') {
		socket.emit('getHistorical');
	}
}

/*
 * Handle anomalies
 */
socket.on('anomaly', function(data) {
	//console.log(data);

	// add anomaly to array
	angular.element(document.getElementById('main')).scope().addAnomaly(data);

	// angular update
	angular.element(document.getElementById('main')).scope().$apply();
});

socket.on('test', function (data) {
 alert(data);
});

/**
 * Sends feedback to node when the user responds to an anomaly.
 * @param	mode			'live' or 'historical'
 * @param	anomalyId		id of the anomaly the feedback is for
 * @param	userResponse	'accept' or 'reject'
 */
function feedback(mode, anomalyId, userResponse) {
	socket.emit('feedback', {
		"mode":			mode,
		"anomalyId":	anomalyId,
		"userResponse":	userResponse
	});
}
