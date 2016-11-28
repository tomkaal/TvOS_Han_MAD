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
        users: []
    }
]

app.get('/', function (req, res) {
    res.sendfile('index.html');
});


io.on('connection', function (socket) {
    socket.on('chat message', function (msg) {
        console.log('message: ' + msg);
    });
    // userObject --> {userId, groupId)

    //socket.on('add_user_to_group', function(userObjectString) {
    //    var userObject = JSON.parse(userObjectString);
    //
    //    var userId = userObject.userId;
    //    var groupId = userObject.groupId;
    //    var groupName = "a123456"; // deze moet opgehaald worden uit de db dmv van de groupId
    //
    //    groups.push(
    //        {
    //            groupId: groupId,
    //            groupName: groupName,
    //            users: [userId]
    //        }
    //    )
    //    //socket.join(groupId)
    //
    //});


    socket.on('add_user_to_group', function (userObjectString) {
        
    });

    socket.on('add_user_to_group', function (userObjectString) {
        var userObject = JSON.parse(userObjectString);

        var userId = userObject.userId;
        var groupId = userObject.groupId;
        var groupName = getGroupName(groupId);

        for (var i = 0; i < groups.length; i++) {
            if (groups[i].groupId === groupId) {
                groups[i].users.push(userId);
            }
        }

        //console.log(groups);
        socket.join(groupName);
        console.log(socket.adapter.rooms);
        console.log(userObject);
        io.in(groupName).emit("conform", userId);// iedereen in een groep
        io.emit("group_response", "message from server");// iedereen
        socket.emit("group_response", "message from server");// terug naar wie het gestuurd heeft
        //socket.emit("group_response", 200);// Voor ok

    });
});


function getGroupName(groupId) {
    return "group_" + groupId;
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