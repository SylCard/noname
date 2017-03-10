var express = require('express');
var app = express();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var net = require('net');
var child_process = require('child_process');
var multer = require('multer');

/*
 * Analysis status
 */
var historicalStatus = 'inactive';
var liveStatus = 'inactive';

/*
 * This array will be populated with anomalies received from the backend.
 * index 0: historical, index 1: live
 */
var anomalies = [[], []];

/*
 * Start the webserver on port 80.
 */
server.listen(80);

/*
 * Set up middleware to serve resources from the Frontend directory.
 * Serve index.html when no page is specified.
 */
app.use(express.static('../Frontend'));

app.get('/', function(req, res) {
	res.sendFile('../Frontend/index.html');
});

app.get('/analysis', function(req, res) {
	res.sendFile('/root/Project/Frontend/analysis.html');
});

app.get('/live', function(req, res) {
	res.sendFile('/root/Project/Frontend/live.html');
});

app.get('/static', function(req, res) {
	res.sendFile('/root/Project/Frontend/historical.html');
});

/*
 * Use mutter to handle form file uploads
 */
app.use(multer({dest:'./uploads/'}).single('staticDataFile'));

/*
 * Disallow crawling.
 */
app.get('/robots.txt', function(req, res) {
	res.sendFile('/root/Project/Webserver/robots.txt');
});

/*
 * Handle file upload.
 */
app.post('/staticFileUpload', function(req, res) {
	// Redirect user to analysis page
	res.send("<script>window.location.replace('../analysis.html')</script>");
	
	// Clear historical array
	anomalies[0] = [];

	// Replace the previously uploaded file with the new one
	// And clear the uploads directory (just to be safe)
	child_process.exec(
			'rm -f /root/Project/staticDataFile.csv; ' +
			'file=$(ls -1 /root/Project/Webserver/uploads | head -1); ' +
			'mv /root/Project/Webserver/uploads/$file ' +
			'/root/Project/staticDataFile.csv; ' +
			'rm -f /root/Project/Webserver/uploads/*',
			{maxBuffer: 1024 * 500},
			function (error, stdout, stderr) {
				if (error) {
					console.log(error.stack);
					console.log('error code: ' + error.code);
					console.log('signal received: ' + error.signal);
				}
			}
	);
	
	// Set historical analysis to active
	historicalStatus = 'active';

	// Start analysis on the newly uploaded file
	historicalProcess = child_process.exec(
		'cd /root/Project/Backend; java ExtractionTester -h /root/Project/staticDataFile.csv 0 >/dev/null',
		function (error, stdout, stderr) {
			if (error) {
				console.log(error.stack);
				console.log('error code: ' + error.code);
				console.log('signal received: ' + error.signal);
			}
		}
	);

	historicalProcess.on('close', (code) => {
		console.log('hist proc. exited');

		// set historical status to inactive
		historicalStatus = 'inactive';
		
		// emit historical status to update UI
		io.emit('historicalStatus', historicalStatus);
	});

});


/*
 * Start a server to listen for anomalies from the backend.
 */
var backendPort = 6969;
var backendHost = '127.0.0.1';

net.createServer(function(socket) {
	console.log("Backend socket connected: " + 
						socket.remoteAddress + ":" + socket.remotePort);
		
	socket.on('data', function(data) {
			
		// Parse the anomaly as JSON
		var anomaly = JSON.parse(data);


		// Add the anomaly to the correct array
		if (anomaly.mode == 0) {
			// Historical

			// Add anomaly to array
			anomalies[0][anomaly.AnomalyID] = anomaly;
			
			// Send anomaly to frontend
			io.emit('anomaly', anomaly);

			// Respond to server
			socket.write(data);
		
		} else if (anomaly.mode == 1) {
			// Live
			
			// Add anomaly to array
			anomalies[1][anomaly.AnomalyID] = anomaly;
			
			// Send anomaly to frontent
			io.emit('anomaly', anomaly);

			// Respond to server
			socket.write(data);
		
		}
	});

	socket.on('close', function(data) {
		console.log("Backend socket closed: " +
						socket.remoteAddress + ":" + socket.remotePort);
	});

}).listen(backendPort, backendHost);

/*
 * socket.io - send anomalies when client connects and when anomalies
 * are requested.
 * Loop through the anomalies array and emit each anomaly separately.
 * Also handles user feedback and initiation of analysis.
 * Also handles starting and stopping of analysis.
 */
io.on('connection', function(socket) {
	function emitAnomalies(mode) {
		anomalies[mode].forEach(function(value) {
			socket.emit('anomaly', value);
		});
	}
	
	socket.on('getLive', function(data) {
		emitAnomalies(1);
	});

	socket.on('getHistorical', function(data) {
		emitAnomalies(0);
	});

	// Handle user feedback
	socket.on('deleteLiveAnomaly', function(data) {
			delete anomalies[1][data];
	});
	socket.on('deleteHistoricalAnomaly', function(data) {
			delete anomalies[0][data];
	});
	
	// Handle status requests
	socket.on('getHistoricalStatus', function() {
		socket.emit('historicalStatus', historicalStatus);
	});
	socket.on('getLiveStatus', function() {
		socket.emit('liveStatus', liveStatus);
	});

	// Handle analysis start
	// Historical analysis is started automatically when file is uploaded
	socket.on('startLiveAnalysis', function(data) {
		// clear live array
		anomalies[1] = [];
			
		// start analysis
		liveProcess = child_process.exec(
			'cd /root/Project/Backend; java ExtractionTester -l ' + data + ' 1 >/dev/null',
			function (error, stdout, stderr) {
				if (error) {
					console.log(error.stack);
					console.log('error code: ' + error.code);
					console.log('signal received: ' + error.signal);
				}
			}
		);

		// set live to active
		liveStatus = 'active';

		// emit live status to update UI
		socket.emit('liveStatus', liveStatus);
	});

	// Handle analysis stop
	socket.on('stopHistoricalAnalysis', function() {
		// kill the child
		// couldn't find a more elegant way to do this
		child_process.exec(
			"kill -9 $(ps -aux | grep 'java ExtractionTester -h' | awk 'FNR == 1 {print $2}');" +
			"kill -9 $(ps -aux | grep 'java ExtractionTester -h' | awk 'FNR == 1 {print $2}');" +
			"kill -9 $(ps -aux | grep 'java ExtractionTester -h' | awk 'FNR == 1 {print $2}');" +
			"kill -9 $(ps -aux | grep 'java ExtractionTester -h' | awk 'FNR == 1 {print $2}');" +
			"kill -9 $(ps -aux | grep 'java ExtractionTester -h' | awk 'FNR == 1 {print $2}')",
			function (error, stdout, stderr) {
				if (error) {
					console.log("killed child");
				}
			}
		);

		// set historical to inactive
		historicalStatus = 'inactive';

		// emit historical status to update UI
		socket.emit('historicalStatus', historicalStatus);
	});
	socket.on('stopLiveAnalysis', function() {
		// kill the child
		// couldn't find a more elegant way to do this
		child_process.exec(
			"kill -9 $(ps -aux | grep 'java ExtractionTester -l' | awk 'FNR == 1 {print $2}');" +
			"kill -9 $(ps -aux | grep 'java ExtractionTester -l' | awk 'FNR == 1 {print $2}');" +
			"kill -9 $(ps -aux | grep 'java ExtractionTester -l' | awk 'FNR == 1 {print $2}');" +
			"kill -9 $(ps -aux | grep 'java ExtractionTester -l' | awk 'FNR == 1 {print $2}');" +
			"kill -9 $(ps -aux | grep 'java ExtractionTester -l' | awk 'FNR == 1 {print $2}')",
			function (error, stdout, stderr) {
				if (error) {
					console.log("killed child");
				}
			}
		);

		// set live to inactive
		liveStatus = 'inactive';

		// emit live status to update UI
		socket.emit('liveStatus', liveStatus);
	});

});

