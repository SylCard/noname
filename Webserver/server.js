var express = require('express');
var app = express();
var child_process = require('child_process');
var githubhook = require('githubhook');
var github = githubhook({ secret:'hello', path:'/postreceive' });

// Listen for GitHub push notifications
github.listen();

// Auto redeploy from GitHub repo on post-receive notification
github.on('*', function (event, repo, ref, data) {
	console.log('Redeploying app...');
	
	child_process.exec(
	'rm -rf /root/Project/github;' +
	'git clone git@github.com:SylCard/noname.git /root/Project/github',
	function (error, stdout, stderr) {
		if (error) {
			console.log(error.stack);
			console.log('Error code: ' + error.code);
			console.log('Signal received: ' + error.signal);
		}
	});

	console.log('App redeployed.');

});

// Set up middleware to serve resources from the Frontend directory
app.use(express.static('../github/Frontend'));

// Serve index.html when no page is specified
app.get('/', function(req, res) {
	res.sendFile('../github/Frontend/index.html');
});

// Start the web server on port 80
var server = app.listen(80, function() {
	var host = server.address().address;
	var port = server.address().port;

	console.log("Webserver listening at http://%s:%s", host, port);
});
