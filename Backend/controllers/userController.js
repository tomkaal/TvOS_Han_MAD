/*jslint node: true */
"use strict";

var mongoose = require('mongoose');
var User = mongoose.model('User');
var Answer = mongoose.model('Answer');
var Team = mongoose.model('Team');
var Question = mongoose.model('Question');
var Quiz = mongoose.model('Quiz');
var asyncEach = require('async/each');

exports.createOne = function (req, res) {
    var doc = new User(req.body);

    doc.save(function (err) {
        if (err) {
            return res.send({
                doc: null,
                err: err
            });
        }

        res.send({
            doc: doc,
            err: err
        });
    });
};



exports.answer = function (req, res) {
    var questionId = req.body.questionId;
    var answerId = req.body.answerId;
    var userId = req.body.userId;
    var correct = false;
    var allUsersAnswered = true;
    var userQuestions = [];
    var userIds = [];
    var usersHaveAnswered = {};

    // Find the question with the correct answer
    Question.findOne({ _id: questionId}, function (err, currentQuestion){
        // Find the answer model the user has given
        Answer.findOne({ _id: answerId}, function (err, currentAnswer){
            // Check if the answer the user has given is the correct answer
            if(String(currentQuestion.correctAnswer) == String(currentAnswer._id)) {
                correct = true;
            }
            // Search for the current user and push the question in the questionarray
            User.findByIdAndUpdate(userId, { $push: {questions: {question: currentQuestion._id, correct: correct}} }).populate('team').exec(function (err, currentUser) {
                // Find the team, the user belongs to.
                User.find({team: currentUser.team}).exec(function (err, teamUsers){
                    asyncEach(teamUsers, function(teamUser, teamUserCallback) {
                        teamUser.populate('questions');
                        userIds.push(teamUser._id);

                        usersHaveAnswered[teamUser._id] = false;
                        teamUser.questions.forEach(function (teamUserQuestion) {
                            // loopt door alle vragen van alle gebruikers heen in het team
                            // controleren of de huidige vraag aanwezig is tussen de vragen van het team
                            if(String(teamUserQuestion.question) == String(currentQuestion._id)){
                                usersHaveAnswered[teamUser._id] = true;
                            } else {
                                usersHaveAnswered[teamUser._id] = false;
                            }

                            // push all the answers of the team members into an array
                            userQuestions.push(teamUserQuestion);
                        });
                        teamUserCallback();
                    }, function(err){
                        for (var i = 0; i < userIds.length; i++){
                            if (usersHaveAnswered[userIds[i]] == false) {
                                allUsersAnswered = false;
                            }
                        }
                        if(allUsersAnswered) {
                            // get the most frequent answer from the users and add it as a team answer
                            // http://stackoverflow.com/questions/3783950/get-the-item-that-appears-the-most-times-in-an-array
                            var frequency = {};  // array of frequency.
                            var max = 0;  // holds the max frequency.
                            var teamQuestion;   // holds the max frequency element.
                            for (var v in userQuestions) {
                                frequency[userQuestions[v]] = (frequency[userQuestions[v]] || 0) + 1; // increment frequency.
                                if (frequency[userQuestions[v]] > max) { // is this frequency > max so far ?
                                    max = frequency[userQuestions[v]];  // update max.
                                    teamQuestion = userQuestions[v];          // update result.
                                }
                            }
                            Team.findByIdAndUpdate(currentUser.team._id, { $push: {questions: {question: teamQuestion.question, correct: teamQuestion.correct}}}, function (err, team){
                                return res.json({doc: {allUsersAnswered: allUsersAnswered}});
                            });
                        } else {
                            return res.json({doc: {allUsersAnswered: allUsersAnswered}});
                        }
                    });
                });
            });
        });
    });
};