var socket = io();

/*
 * Functions to activate and deactivate tabs
 */
 function activateHistorical() {
 	//$('#historicalTab').css('pointer-events', '');
 	$('#historicalTab .status').text('active');
 	$('#historicalTab .status').addClass('label-success');
 }
 function activateLive() {
 	//$('#liveTab').css('pointer-events', '');
 	$('#liveTab .status').text('active');
 }

 function deactivateHistorical() {
 	//$('#historicalTab').css('pointer-events', 'none');
 	$('#historicalTab .status').text('inactive');
 }
 function deactivateLive() {
 	//$('#liveTab').css('pointer-events', 'none');
 	$('#liveTab .status').text('inactive');
 }

/*
 * Functions to delete anomalies
 */
function deleteLiveAnomaly(id) {
	// remove from array
	angular.element(document.getElementById('main')).scope().removeAnomaly(id);

	// angular update
	angular.element(document.getElementById('main')).scope().$apply();

	// remove from node
	socket.emit('deleteLiveAnomaly', id);
}
function deleteHistoricalAnomaly(id) {
	// remove from array
	angular.element(document.getElementById('main')).scope().removeAnomaly(id);

	// angular update
	angular.element(document.getElementById('main')).scope().$apply();

	// remove from node
	socket.emit('deleteHistoricalAnomaly', id);
}

/*
 * Check status and handle response
 */
function getStatus(mode) {
	if (mode == 'historical') {
		socket.emit('getHistoricalStatus');
	} else if (mode == 'live') {
		socket.emit('getLiveStatus');
	}
}
socket.on('historicalStatus', function(data) {
	if (data == 'active') {
		// historical running
		// activate historical tab
		activateHistorical();

		// If we're on the analysis page, also update the page
		if (currentPage == 'analysis') {
			$('#historicalSource').attr('disabled', 'disabled');
			$('#historicalStatus').text('active');
			$('#historicalStatus').removeClass('label-default');
			$('#historicalStatus').addClass('label-success');

			$('#historicalStartBtn').text('Stop analysis');
			$('#historicalStartBtn').removeClass('btn-primary');
			$('#historicalStartBtn').addClass('btn-danger');
		}
	} else if (data == 'inactive') {
		// historical stopped
		// leave historical tab deactivated
		deactivateHistorical();

		// If we're on the analysis page, also update the page
		if (currentPage == 'analysis') {
			$('#historicalSource').removeAttr('disabled');
			$('#historicalStatus').text('inactive');
			$('#historicalStatus').removeClass('label-success');
			$('#historicalStatus').addClass('label-default');

			$('#historicalStartBtn').text('Start analysis');
			$('#historicalStartBtn').removeClass('btn-danger');
			$('#historicalStartBtn').addClass('btn-primary');
		}
	}
});
socket.on('liveStatus', function(data) {
	if (data == 'active') {
		// live running
		// activate live tab
		activateLive();

		// If we're on the analysis page, also update the page
		if (currentPage == 'analysis') {
			$('#liveSource').attr('disabled', 'disabled');
			$('#liveStatus').text('active');
			$('#liveStatus').removeClass('label-default');
			$('#liveStatus').addClass('label-success');

			$('#liveStartBtn').text('Stop analysis');
			$('#liveStartBtn').removeClass('btn-primary');
			$('#liveStartBtn').addClass('btn-danger');
		}
	} else if (data == 'inactive') {
		// live stopped
		// leave live tab deactivated
		deactivateLive();

		// If we're on the analysis page, also update the page
		if (currentPage == 'analysis') {
			$('#liveSource').removeAttr('disabled');
			$('#liveStatus').text('inactive');
			$('#liveStatus').removeClass('label-success');
			$('#liveStatus').addClass('label-default');

			$('#liveStartBtn').text('Start analysis');
			$('#liveStartBtn').removeClass('btn-danger');
			$('#liveStartBtn').addClass('btn-primary');
		}
	}
});

// Sync page when document loads
$('document').ready(function() {
	// disable tabs initially
	deactivateHistorical();
    deactivateLive();

	// get historical status
	getStatus('historical');

	// get live status
	getStatus('live');
});

// Get anomalies
$('document').ready(function() {
	if (currentPage == 'live') {
		getAnomalies('live');

	} else if (currentPage == 'historical') {
		getAnomalies('historical');
		
	}
});
