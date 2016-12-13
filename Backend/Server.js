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

var userSocketMap = []

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

app.get('/', function (req, res) {
    res.sendfile('index.html');
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

      socket.on('chat', function (val) {
                io.emit('chat', val);
                console.log(val);
      });
      
      socket.on('tvJoinRoom', function(data) {
                console.log('tv joined room!');
                socket.join(getTvRoomName());
        });
      
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
        };

        console.log(responseObject);
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

        for ( var i = 0; i < teamArray.length; i++ ){
            for ( var j = 0; j < teamArray[i].users.length; j++) {

                var userId = teamArray[i].users[j].userId;
                console.log(userId);
                console.log(io.sockets.connected);
                var userSocket = io.sockets.connected[socketIdList[userId]];
                console.log(userSocket);
                userSocket.join(getTeamRoomName(teamArray[i].teamId));
            }
        }

        console.log("teams created");
        console.log(groupId);
        var acceptedGroupName = getAcceptedGroupName(groupId, getGroupName(groupId));
        console.log(acceptedGroupName);
        io.in(acceptedGroupName).emit("teams_created", teamObject);// aan iedereen in de groep
    });


    socket.on('notify_tv', function (teamId) {
        io.in(getTvRoomName()).emit("team_nearby", teamId);// naar tv

        //--------------test----------//
        //var tvResponseObject = "{"+
        //    "\"teamId\": \"" + teamId + "\","+
        //    "\"questionId\": \"hd94hr743hr93h4\","+
        //    "\"answerIds\": [ \"1111111111111\", \"2222222222222\", \"3333333333333\", \"4444444444444\"]}";
        //console.log(tvResponseObject)
        //io.in(getTeamRoomName(teamId)).emit("tv_notified", tvResponseObject);// aan iedereen in het team

        //--------------test----------//

    });

    socket.on('tv_is_ready', function (tvResponseObject) {
        var tvResponse = JSON.parse(tvResponseObject);// teamId, questionId, answerIds
        var teamId = tvResponse.teamId;
        io.in(getTeamRoomName(teamId)).emit("tv_notified", tvResponseObject.toString());// aan iedereen in het team
    });

    socket.on('all_users_answered', function (object) {
        var answerObject = JSON.parse(object);
        var teamId = answerObject.teamId;
        var questionId = answerObject.questionId;
        io.in(getTvRoomName()).emit("team_has_answered", {teamId: teamId, questionId: questionId});// aan apple tv
        io.in(getTeamRoomName(teamId)).emit("all_users_answered");// aan iedereen in het team
    });

        //team id meegeven aan apple tv --> kaas
});

function getTvRoomName(){
    return "tv_room";
}

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

function getTeamRoomName(teamId){
    return "team_" + teamId;
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


