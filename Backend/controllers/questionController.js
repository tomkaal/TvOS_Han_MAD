/*jslint node: true */
"use strict";

var mongoose = require('mongoose');
var User = mongoose.model('User');
var Answer = mongoose.model('Answer');
var Team = mongoose.model('Team');
var Question = mongoose.model('Question');

exports.answer = function (req, res) {
    var quizId = req.body.quizId;
    var questionId = req.body.questionId;
    var userId = req.body.userId;

    Quiz.findOne({ _id: quizId}, function(err, quiz){
        Question.findOne($and [{ _id: questionId}, {quiz: quiz}], function (err, question){
            User.findByIdAndUpdate(userId, { $push: { questions: [question]}}, { new: true }, function () {
                return res.status(200).send();
            });
        });
    });
};