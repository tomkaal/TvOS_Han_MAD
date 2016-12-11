/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    Team = mongoose.model('Team'),
    User = mongoose.model('User'),
    asyncEach = require('async/each');

function sendErr(res, err) {
    return res.json({
        doc: null,
        err: err
    });
}

// Controller function that the frontend will use to start a game
exports.createTeams = function (req, res) {
    var data = req.body;

    var doc =  {
        groupId: data.groupId,
        teams: []
    };

    asyncEach(data.teams,
        function(team, teamsCallback) {
            var newTeam = new Team({
                name: team.name,
                group: data.groupId
            });

            newTeam.save(function (err) {
                if (err) { return sendErr(res, err); }
                var docTeam  = {
                    teamId: newTeam._id,
                    users: []
                };
                //update all users with their associated team
                asyncEach(team.users,
                    function(user, usersCallback) {
                        User.update({ _id: user.id }, { $set: { team: newTeam._id }}, function (err) {
                            //User updated, check for err
                            if (err) { return sendErr(res, err); }
                            docTeam.users.push({userId: user.id});
                            usersCallback(); //tells the asyncEach function for users that the this iteration is done
                        });
                },
                    function(err) {
                        if (err) { return sendErr(res, err); }
                        doc.teams.push(docTeam);
                        teamsCallback(); //tells the asyncEach function for teams that the this iteration is done
                });
            });
        },
        function(err) {
            if (err) { return sendErr(res, err); }
            //All teams are added and users are updated, give response
            return res.json({
                doc: doc,
                err: err
            });
        });
};

exports.score = function (req, res) {
    var teamId = req.params.teamId;
    var totalScore = 0;

    Team.findOne({ _id: teamId}, function(err, team) {
        team.populate('questions');
        asyncEach(team.questions,
            function(question, questionCallback) {
                if(question == null) {
                    return;
                }
                if(question.correct == true) {
                    totalScore += question.score;
                }
                questionCallback();
            });

        // team.questions.forEach(function(question) {
        //     if(question == null) {
        //         return;
        //     }
        //     if(question.correct == true) {
        //         totalScore += question.score;
        //     }
        // });
        return(res.json({doc: totalScore}));
    });
};
