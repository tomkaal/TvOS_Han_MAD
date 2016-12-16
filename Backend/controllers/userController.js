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

/*
----------------- 1 user in 1 team
JSON: Case 1 enige user in het team voegt het goede antwoord toe
{
    "userId": "584ea00719f29f1990b042d6",
    "questionId": "584e93d2772ac0143cbd6e61",
    "answerId": "584e93d2772ac0143cbd6e65"
}
verwacht resultaat:
allUsersAnswered: true
teamAnswer: 584e93d2772ac0143cbd6e65

----------------- 3 users in 1 team
JSON: Case 2 user voegt fout antwoord toe
{
    "userId": "584ea01a19f29f1990b042d7",
    "questionId": "584e93d2772ac0143cbd6e61",
    "answerId": "584e93d2772ac0143cbd6e69"
}
Verwacht resultaat:
    allUsersAnswered: false
    userAnswer: 584e93d2772ac0143cbd6e69
    userCorrect: false
    teamAnswer: null
    teamCorrect: null

JSON: Case 3 user voegt fout antwoord toe
{
    "userId": "584ea0ae19f29f1990b042d8",
    "questionId": "584e93d2772ac0143cbd6e61",
    "answerId": "584e93d2772ac0143cbd6e69"
}
verwacht resultaat:
    allUsersAnswered: false
    userAnswer: 584e93d2772ac0143cbd6e69
    userCorrect: false
    teamAnswer: null
    teamCorrect: null

JSON: Case 4 laatste user voegt het goede antwoord toe
{
    "userId": "584ea0bc19f29f1990b042d9",
    "questionId": "584e93d2772ac0143cbd6e61",
    "answerId": "584e93d2772ac0143cbd6e65"
}
verwacht resultaat:
    allUsersAnswered: false
    userAnswer: 584e93d2772ac0143cbd6e61
    userCorrect: true
    teamAnswer: 584e93d2772ac0143cbd6e69
    teamCorrect: false
*/
exports.answer = function (req, res) {
    // get the current question id
    var questionId = req.body.questionId
    // get the current answer id
    var answerId = req.body.answerId;
    // get the current user id
    var userId = req.body.userId;
    // boolean to check if all the users in the team have answered
    var allUsersInTeamHaveAnswered = true;
    var teamUsersArray = [];
    var frequency = {};  // array of frequency.
    var max = 0;  // holds the max frequency.
    var teamAnswer;   // holds the max frequency element.

    // Find the question with the correct answer
    Question.findOne({ _id: questionId}, function (err, currentQuestion){
        // Find the answer the user has given
        Answer.findOne({ _id: answerId}, function (err, currentAnswer){
            // Search for the current user and push the question in the questionarray
            User.findByIdAndUpdate(userId, { $push: {questions: {question: currentQuestion._id, answer: currentAnswer._id}} }).populate('team').exec(function (err, currentUser) {
                // Find all the users in the current team
                User.find({team: currentUser.team}).exec(function (err, teamUsers){
                    // Loop through all the users in the current team
                    asyncEach(teamUsers, function(teamUser, callback) {
                        teamUser.populate('questions');

                        var teamUserObject = {answer:null}
                        teamUser.questions.forEach(function (teamUserQuestion) {
                            // loopt door alle vragen van alle gebruikers heen in het team
                            // controleren of de huidige vraag aanwezig is tussen de vragen van het team
                            if(String(teamUserQuestion.question) == String(currentQuestion._id)){
                                teamUserObject.answer = teamUserQuestion.answer
                            }
                        });
                        teamUsersArray.push(teamUserObject);
                        callback();
                    }, function(err){
                        asyncEach(teamUsersArray, function(teamUser, teamUsersArrayCallback) {
                            if(teamUser.answer == null) {
                                allUsersInTeamHaveAnswered = false;
                            }
                            teamUsersArrayCallback();
                        }, function(err){
                            if(allUsersInTeamHaveAnswered) {
                                asyncEach(teamUsersArray, function(teamUser, allUsersInTeamHaveAnsweredCallback) {
                                    // get the most frequent answer from the users and add it as a team answer
                                    frequency[teamUser.answer]=(frequency[teamUser.answer] || 0)+1; // increment frequency.
                                    if(frequency[teamUser.answer] > max) { // is this frequency > max so far ?
                                        max = frequency[teamUser.answer];  // update max.
                                        teamAnswer = teamUser.answer;      // update result.
                                    }
                                    console.log(teamAnswer);
                                    allUsersInTeamHaveAnsweredCallback();
                                }, function(err){
                                    Team.findByIdAndUpdate(currentUser.team._id, { $push: {questions: {question: currentQuestion, answer: teamAnswer}}}, function (){
                                        return res.json({doc: {allUsersAnswered: allUsersInTeamHaveAnswered}});
                                    });
                                });
                            } else {
                                return res.json({doc: {allUsersAnswered: allUsersInTeamHaveAnswered}});
                            }
                        });
                    });
                });
            });
        });
    });
};