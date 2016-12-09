/**
 * Created by tommi on 18-11-2016.
 */
var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var groups = [
    {
        groupId: "aaaaa",
        groupName: "Klas 4",
        userId: "u1",
        userName: "Tom",
        users: ["u1", "u2", "u3"],
    },
    {
        groupId: "bbbbb",
        groupName: "Klas 8",
        userId: "wesleyEgberts",
        userName: "Wesley Egberts",
        users: ["wesleyEgberts"],
    },
    {
        groupId: "ccccc",
        groupName: "De tijgers",
        userId: "dennisDulos",
        userName: "Dennis Dulos",
        users: ["dennisDulos"],
    }
]

var userIdSocketIdList = [];

app.get('/', function (req, res) {
    res.sendfile('index.html');
});


io.on('connection', function (socket) {

    socket.on('get_groups', function (userObjectString) {
        console.log("get_groups");
        socket.emit("update_groups", groups);
    });

    socket.on('create_group', function (userObjectString) {

        groups.push({
            groupId: guid(),
            groupName: guid(),
            userId: "dennisDulos",
            userName: "Dennis Dulos",
            users: ["dennisDulos"]
        })
        console.log("create_groups");
        console.log(groups);

        io.emit("update_groups", groups);
    });

    socket.on('swap_user', function (userObjectString) {
        var userObject = JSON.parse(userObjectString);

        var userId = userObject.userId;
        var groupId = userObject.groupId;

    });
    socket.on('add_to_accepted_users', function (userObjectString) {
        var userObject = JSON.parse(userObjectString);

        var userId = userObject.userId;
        var groupId = userObject.groupId;
        var userSocket = io.sockets.connected[userIdSocketIdList[userId]];
        //console.log(io.sockets.connected);
        userSocket.join("accepted_" + groupId);
        console.log(userSocket);


    });

    socket.on('remove_to_accepted_users', function (userObjectString) {
        var userObject = JSON.parse(userObjectString);

        var userId = userObject.userId;
        var groupId = userObject.groupId;
        var userSocket = io.sockets.connected[userIdSocketIdList[userId]];
        //console.log(io.sockets.connected);
        userSocket.leave("accepted_" + groupId, function(err){

        });

    });

    socket.on('disconnect', function () {
        var index = userIdSocketIdList.indexOf(socket.id);
        userIdSocketIdList.splice(index, 1);
    });

    socket.on('add_user_to_group', function (userObjectString) {
        var userObject = JSON.parse(userObjectString);

        var userId = userObject.userId;
        var groupId = userObject.groupId;


        for (var i = 0; i < groups.length; i++) {
            if (groups[i].groupId === groupId) {
                if(groups[i].users[0] !== userId){
                    groups[i].users.push(userId);
                }
            }
        }

        //console.log(groups);
        console.log("Add user to group")
        socket.join(groupId);
        var socketId = socket.id;
        userIdSocketIdList[userId] = socketId;
        console.log(userIdSocketIdList);
        io.in(groupId).emit("conform", userId);// iedereen in een groep
        io.emit("group_response", "message from server");// iedereen
        socket.emit("group_response", "message from server");// terug naar wie het gestuurd heeft
        //socket.emit("group_response", 200);// Voor ok

    });

    setTimeout(function(){
        alert("Hello");
    }, 3000);

});

function generateUserArrays(groupIndex){

    var usersIdNameArray = [];
     for( var i = 0; i < groups[groupIndex].users.length; i++ ){
         usersIdNameArray.push({
             userId: groups[groupIndex].users[i],
             userName: "Gebruiker" + (i + 1)
         })
     }
    return usersIdNameArray;
}

function getGroupName(groupId) {
    return groupId;
}

function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
}


http.listen(3000, function () {
    console.log('listening on *:3000');
});