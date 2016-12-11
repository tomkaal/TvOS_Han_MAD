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
    var quizId = req.body.quizId;
    var questionId = req.body.questionId;
    var answerId = req.body.answerId;
    var userId = req.body.userId;
    var correct = false;
    var allUsersAnswered = true;
    var userQuestions = [];

    // The quiz has the question with the correct answer
    Quiz.findOne({ _id: quizId}, function(err, quiz){
        // Find the question with the correct answer
        Question.findOne({ _id: questionId, quiz: quiz}, function (err, currentQuestion){
            // Find the answer model the user has given
            Answer.findOne({ _id: answerId, quiz: quiz}, function (err, currentAnswer){
                console.log(currentAnswer, currentQuestion, quiz);
                // Check if the answer the user has given is the correct answer
                if(currentQuestion.correctAnswer == currentAnswer) {
                    correct = true;
                }
                // Search for the current user and push the question in the questionarray
                User.findByIdAndUpdate(userId, { $push: {questions: [currentQuestion, correct]} }, function (err, currentUser) {
                    currentUser.populate('team');
                    // Find the team, the user belongs to.
                    Team.findById(currentUser.team._id, function (err, currentTeam){
                        currentTeam.populate('users');

                        // Loop through each user, to get all questions and add the most frequent question to the team
                        asyncEach(currentTeam.users,
                            function(teamUser, teamUserCallback) {
                                teamUser.populate('questions');
                                if(!teamUser.questions.contains(currentQuestion)) {
                                    allUsersAnswered = false;
                                }
                                asyncEach(teamUser.questions,
                                    function(teamUserQuestion, teamUserQuestionCallback) {
                                        if (teamUserQuestion == currentQuestion) {
                                            userQuestions.push(teamUserQuestion);
                                        }
                                        teamUserQuestionCallback();
                                    });
                                teamUserCallback();
                            });
                        // oude versie niet async
                        // currentTeam.users.forEach(function(teamUser) {
                        //     teamUser.populate('questions');
                        //     if(!teamUser.questions.contains(currentQuestion)) {
                        //         allUsersAnswered = false;
                        //     }
                        //
                        //     async maken
                        //     teamUser.questions.forEach(function (teamUserQuestion) {
                        //         if (teamUserQuestion == currentQuestion) {
                        //             userQuestions.push(teamUserQuestion);
                        //         }
                        //     });
                        // });

                        if(allUsersAnswered) {
                            // http://stackoverflow.com/questions/3783950/get-the-item-that-appears-the-most-times-in-an-array
                            var frequency = {};  // array of frequency.
                            var max = 0;  // holds the max frequency.
                            var result;   // holds the max frequency element.
                            for (var v in userQuestions) {
                                frequency[userQuestions[v]] = (frequency[userQuestions[v]] || 0) + 1; // increment frequency.
                                if (frequency[userQuestions[v]] > max) { // is this frequency > max so far ?
                                    max = frequency[userQuestions[v]];  // update max.
                                    result = userQuestions[v];          // update result.
                                }
                            }

                            currentTeam.questions.add(result).save();
                            return (res.json({doc: currentQuestion.score}))
                        }
                    });
                    return res.status(200).send();
                });
            });
        });
    });
};