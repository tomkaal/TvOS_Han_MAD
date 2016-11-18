/**
 * Created by tommi on 18-11-2016.
 */
var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

var groups = {};
app.get('/', function(req, res){
    res.sendfile('index.html');
});

io.on('connection', function(socket){
    socket.on('chat message', function(msg){
        console.log('message: ' + msg);
    });

    socket.on('Create group', function(id){
        groups.push({groupId: id})
        console.log('message: ' + id);
    });


});

var createGroup = function(id){
    return {
        groupId: id,
        participants: {
            user
        }
    }
}




http.listen(3000, function(){
    console.log('listening on *:3000');
});