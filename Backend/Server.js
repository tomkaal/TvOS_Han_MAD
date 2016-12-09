var express = require("express");
var app = express();
var path = require('path');
var fs = require('fs');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require("express-session");
var mongoose = require('mongoose');
var http = require('http');
var socketio = require('socket.io');


var httpServer = http.Server(app);
var io = socketio(httpServer);
// Load configuration
var env = process.env.NODE_ENV || 'development',
    config = require('./config.js')[env];

// Bootstrap db connection
var mongoose = require('mongoose'),
    Schema = mongoose.Schema;
mongoose.connect(config.db);

mongoose.connection.on('error', function (err) {
    "use strict";
    console.error('MongoDB error: %s', err);
});

// Set debugging on/off
if (config.debug) {
    mongoose.set('debug', true);
} else {
    mongoose.set('debug', false);
}


//Bootstrap models
var models_path = __dirname + '/models',
    model_files = fs.readdirSync(models_path);
model_files.forEach(function (file) {
    require(models_path + '/' + file);
});

// Configure body-parser
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));     // Notice because option default will flip in next major; http://goo.gl/bXjyyz
app.use(cookieParser());

var sessionMiddleware = session({
    secret: "madtvos",
    resave: true,
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

var groups = [];
var teams = [];
var socketIdList = [];

io.on('connection', function (socket) {
    console.log("socket connected");

    socket.on('add_user_to_group', function (userObjectString) {
        var userObject = JSON.parse(userObjectString);

        var userId = userObject.userId;
        var userName = userObject.userName;
        var groupId = userObject.groupId;
        var groupName = userObject.groupName;
        var isFound = false;
        var generalGroupName = getGeneralGroupName(groupId, groupName)
        socket.join(generalGroupName);// join socket group

        for (var i = 0; i < groups.length; i++) { // check id group exists, if so add new user
            if (groups[i].groupId === groupId) {
                groups[i].users.push(
                    {
                        userId: userId,
                        userName: userName
                    }
                );
                io.in(generalGroupName).emit("users_in_group", groups[i].users);
                isFound = true;
            }
        }

        if (!isFound) {
            groups.push(generateNewGroup(groupId, userId, groupName, userName)); // if group does not exist, add new group
            io.emit("update_groups", groups);
        }
        socketIdList[userId] = socket.id; // save a link of the socketID with the userID

        console.log(groups);
        console.log(socketIdList);

        var responseObject = {
            userName: userName,
            userId: userId
        }
        io.in(generalGroupName).emit("user_list_changed", responseObject);// aan iedereen in de groep
    });

    socket.on('get_groups', function () {
        socket.emit("update_groups", groups);
    });

    socket.on('get_users_in_group', function (arg) {// only groupId String
        var groupId = arg.toString();

        for(var i = 0; i < groups.length; i ++){
            if(groups[i].groupId === groupId){
                socket.emit("users_in_group", groups[i].users);
            }
        }
    });

    socket.on('join_user_in_acceptance_group', function (userObjectString) {
        var userObject = JSON.parse(userObjectString);

        var userId = userObject.userId;
        var groupId = userObject.groupId;
        var userSocket = io.sockets.connected[socketIdList[userId]];
        userSocket.join(getAcceptedGroupName(groupId, getGroupName(groupId)));
        console.log("Join " + userSocket);
        console.log(io.nsps["/"].adapter.rooms[getAcceptedGroupName(groupId, getGroupName(groupId))]);
    });

    socket.on('remove_user_in_acceptance_group', function (userObjectString) {
        var userObject = JSON.parse(userObjectString);

        var userId = userObject.userId;
        var groupId = userObject.groupId;
        var userSocket = io.sockets.connected[socketIdList[userId]];
        userSocket.leave(getAcceptedGroupName(groupId, getGroupName(groupId)));
        console.log("Leave " + userSocket);
        console.log(io.nsps["/"].adapter.rooms[getAcceptedGroupName(groupId, getGroupName(groupId))]);

    });

    socket.on('accept_group_organizing', function (receivedGroupId) {//Geef aan of mensen geaccepteerd zijn of niet
        var groupId = receivedGroupId.toString();
        var generalGroupName = getGeneralGroupName(groupId, getGroupName(groupId));
        var acceptedGroupName = getAcceptedGroupName(groupId, getGroupName(groupId));
        io.in(acceptedGroupName).emit("group_accepted");// aan iedereen in de groep
        io.in(generalGroupName).emit("group_denied");// aan iedereen in de groep
    });

    socket.on('teams_created_from_owner', function (teamObjectString) {
        console.log(teamObjectString)
        var teamObject = JSON.parse(teamObjectString);

        var groupId = teamObject.groupId;
        var teamArray = teamObject.teams;

        console.log("teams created");
        console.log(groupId);
        var acceptedGroupName = getAcceptedGroupName(groupId, getGroupName(groupId));
        console.log(acceptedGroupName);
        io.in(acceptedGroupName).emit("teams_created", teamObject);// aan iedereen in de groep
    });


});

function getGeneralGroupName(groupId, groupName) {
    return "general_" + groupName + "_" + groupId;
}

function getAcceptedGroupName(groupId, groupName) {
    return "accepted_" + groupName + "_" + groupId;
}

function getGroupName(groupId){
    for(var i = 0; i < groups.length; i++){
        if(groups[i].groupId === groupId){
            return groups[i].groupName;
        }
    }
    return "Not found";
}

function generateNewGroup(groupId, userId, groupName, userName) {
    return {
        groupId: groupId,
        groupName: groupName,
        userId: userId,
        userName: userName,
        users: [
            {
                userId: userId,
                userName: userName
            }
        ]
    }
}

// Listen to this Port
httpServer.listen(3000, function () {
    console.log("Live at Port 3000");
});


