/*jslint node: true */
"use strict";

// var mongoose = require('mongoose');
// var User = mongoose.model('User');
// var Answer = mongoose.model('Answer');
// var Team = mongoose.model('Team');
// var Question = mongoose.model('Question');
//
// exports.answer = function (req, res) {
//     var quizId = req.body.quizId;
//     var questionId = req.body.questionId;
//     var answerId = req.params.answerId;
//     var userId = req.body.userId;
//     var teamId = req.body.teamId;
//     var correct = false;
//
//     Quiz.findOne({ _id: quizId}, function(err, quiz){
//         Question.findOne($and [{ _id: questionId}, {quiz: quiz}], function (err, question){
//             Answer.findOne($and [{ _id: answerId}, {quiz: quiz}], function (err, answer){
//                 if(Question.correctAnswer == answer)
//                     correct = true;
//
//                 User.findByIdAndUpdate(userId, { $push: {questions: [question, correct]} }, function (err, user) {
//                     user.team.
//                     return res.status(200).send();
//                 });
//             });
//         });
//     });
// };