var express = require('express');

var app = express();
app.use('/', express.static('../app/'));
app.use('/bower_components', express.static('../bower_components/'));

var http = require('http').Server(app);
var io = require('socket.io')(http);

var bodyParser = require('body-parser');
var jsonParser = bodyParser.json();
var urlencodedParser = bodyParser.urlencoded({ extended: false });

var anomalies = [
        {
            'anomalyID' : 000001 ,
            'type' : 'Fat Finger' ,
            'yaxis' : [0,0,0,0,0] ,
            'xaxis' : [0,0,0,0,0] ,
            'stock' : ['2017-02-27 20:54:29.933994','locke@rochdaleassets.com','n.ainsworth@vinvest.com','6582.38','25716','GBX','RB.L','Consumer Goods','6582.38','6582.38']
        }
    ];

app.get('/anomalies', function (req, res) {
    'use strict';

    res.send(anomalies);
});

app.post('/anomalies', jsonParser, function (req, res) {
    'use strict';

    if (!req.body) {
        return res.sendStatus(400);
    }
    anomalies.push(req.body);

    io.emit('anomaly', req.body);
    return res.sendStatus(200);
});

http.listen(3000, function () {
    'use strict';
});
