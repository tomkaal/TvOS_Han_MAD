/**
 * Created by tommi on 18-11-2016.
 */
var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var id = ""
var groups = [];
app.get('/', function(req, res){
    res.sendfile('index.html');
});

io.on('connection', function(socket){
    socket.on('chat message', function(msg){
        console.log('message: ' + msg);
    });

    socket.on('Create group', function(name){
        id = 1;
        groups.push(createGroup(id, name));
        console.log('group created with userID: ' + id);
        console.log(groups[0]);
    });

    socket.on('Join group', function(name){
        id = 1;
        for(var i = 0; i < groups.length; i++){
            if(groups[i].groupId === id ){
                groups[i].participants.push(createUser(name, id))
            }
        }

        console.log('User joined with userID: ' + id + ' in group 1');
        console.log(groups);
    });


});

function createGroup(id, name){
    return {
        groupId: id,
        participants: [
            {
                userID: id,
                isGroupOnwer: true,
                userName: name
            }
        ]
    }
}

function createUser(name, id){
    return {
        userID: id,
        isGroupOnwer: false,
        userName: name
    }
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


http.listen(3000, function(){
    console.log('listening on *:3000');
});