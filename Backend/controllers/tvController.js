/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    Tv = mongoose.model('Tv'),
    Quiz = mongoose.model('Quiz'),
    Question = mongoose.model('Question');

function sendErr(res, err) {
    return res.json({
        doc: null,
        err: err
    });
}

exports.retrieveOne = function (req, res) {
    Tv.find({beaconId: req.params.beaconId}, function (err, doc) {
        if (err) { return sendErr(res, err); }

        return res.json({
            doc: doc,
            err: err
        });
    });
};

function shuffle(array) {
    var currentIndex = array.length, temporaryValue, randomIndex;

    // While there remain elements to shuffle...
    while (0 !== currentIndex) {

        // Pick a remaining element...
        randomIndex = Math.floor(Math.random() * currentIndex);
        currentIndex -= 1;

        // And swap it with the current element.
        temporaryValue = array[currentIndex];
        array[currentIndex] = array[randomIndex];
        array[randomIndex] = temporaryValue;
    }

    return array;
}

exports.retrieveQuestion = function (req, res) {
    Tv.findOne({beaconId: req.params.beaconId}, function (err, tv) {
        if (err) { return sendErr(res, err); }

        Quiz.find({tv: tv._id}, function (err, quizzes) {
            var quiz = quizzes.shift();

            Question.find({quiz: quiz._id}).populate("correctAnswer answers").exec(function (err, questions) {
                var question = questions[Math.floor(Math.random() * questions.length)];

                var questionDoc = {
                    _id: question._id,
                    quiz: question.quiz,
                    text: question.text,
                    score: question.score,
                    answers: []
                };

                questionDoc.answers = question.answers;
                questionDoc.answers.push(question.correctAnswer);
                questionDoc.answers = shuffle(questionDoc.answers);

                console.log(questionDoc);

                return res.json({
                    doc: questionDoc,
                    err: err
                });
            });
        });
    });
};


exports.createOne = function (req, res) {
    var doc = new Tv(req.body);

    doc.save(function (err) {
        if (err) { return sendErr(res, err); }

        return res.json({
            doc: doc,
            err: err
        });
    });
};