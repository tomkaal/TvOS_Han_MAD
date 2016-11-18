var express = require("express");
var app = express();
var path = require('path');
var fs = require('fs');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require("express-session");
var mongoose = require('mongoose');

// Bootstrap models
// var models_path = __dirname + '/models',
//     model_files = fs.readdirSync(models_path);
// model_files.forEach(function (file) {
//     require(models_path + '/' + file);
// });

// Configure body-parser
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));     // Notice because option default will flip in next major; http://goo.gl/bXjyyz
app.use(cookieParser());

var sessionMiddleware = session({
    secret:            "De Kaashaas",
    resave:            true,
    saveUninitialized: true
});
app.use(sessionMiddleware);

// Bootstrap routes
var routes_path = __dirname + '/routes',
    route_files = fs.readdirSync(routes_path);
route_files.forEach(function (file) {
    var route = require(routes_path + '/' + file);      // Get the route
    app.use('/api', route);                             // This is our route middleware
});


app.all('*', function (req, res) { // Catch all for unmatched routes
    res.status(404)        // HTTP status 404: NotFound
        .send('Not found');
});

// Listen to this Port
app.listen(3000,function(){
    console.log("Live at Port 3000");
});