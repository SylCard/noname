/*
 * Start analysis
 */
function startAnalysis(mode, dataSource) {
	if (mode == 'historical') {
		// start historical analysis
		//socket.emit('startHistoricalAnalysis', dataSource);

		// upload file
		$("#staticFileUpload").submit();

	} else if (mode == 'live') {
		// start live analysis
		socket.emit('startLiveAnalysis', dataSource);
	}
}

$('document').ready(function() {
	$('#historicalStartBtn').click(function() {
		if ($('#historicalStatus').text() == 'inactive') {
			if ($('#historicalSource').val() != '') {

				// start analysis
				startAnalysis('historical', $('#historicalSource').val());

			} else {
				alert('Please select a data file.');
			}
		} else {
			// stop analysis
			stopAnalysis('historical');
		}
	});

	$('#liveStartBtn').click(function() {
		if ($('#liveStatus').text() == 'inactive') {
			if ($('#liveSource').val() != '') {

				// start analysis
				startAnalysis('live', $('#liveSource').val());

			} else {
				alert('Please enter a stream source.');
			}
		} else {
			// stop analysis
			stopAnalysis('live');
		}
	});
});


/*
 * Stop analysis
 */
function stopAnalysis(mode) {
	if (mode == 'historical') {
		socket.emit('stopHistoricalAnalysis');
		deactivateHistorical();
	} else if (mode == 'live') {
		socket.emit('stopLiveAnalysis');
		deactivateLive();
	}
}