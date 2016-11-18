/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    Round = mongoose.model('Team');

exports.retrieveAll = function (req, res) {
    Round.find(function (err, doc) {
        if (err) {
            return res.send({
                doc: null,
                err: err
            });
        }

        res.json({
            doc: doc,
            err: err
        });
    });
};

exports.retrieveOne = function (req, res) {
    Round.findOne({ _id: req.params.id}, function (err, doc) {
        if (err) {
            return res.send({
                doc: null,
                err: err
            });
        }

        res.json({
            doc: doc,
            err: err
        });
    });
};

exports.createOne = function (req, res) {
    var doc = new Round(req.body);

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

exports.updateOne = function (req, res) {
    Round.findOne({ _id: req.params.id }, function (err, doc) {
        var prop;
        if (err) {
            return res.send({
                doc: null,
                err: err
            });
        }

        for (prop in req.body) {
            if (req.body.hasOwnProperty(prop)) {
                doc[prop] = req.body[prop];
            }
        }

        // save the doc
        doc.save(function (err) {
            if (err) {
                return res.send({
                    doc: null,
                    err: err
                });
            }

            res.json({
                doc: doc,
                err: err
            });
        });
    });
};

exports.deleteOne = function (req, res) {
    Round.remove({
        _id: req.params.id
    }, function (err, doc) {
        if (err) {
            return res.send({
                doc: null,
                err: err
            });
        }

        res.json({
            doc: null,
            err: err
        });
    });
};

exports.startRound = function (req, res) {
    if (req.session.username) {
        var round = new Round({
            categories: req.body.categories,
            questions: [],
            status: true,
            sentin: []
        });

        round.save(function (err) {
            if (err) {
                return res.send({
                    doc: null,
                    err: err
                });
            }

            var Quiz = mongoose.model('Quiz');
            Quiz.update({_id: req.body.quizId}, {$push: {rounds: round._id}}, {upsert: true}, function (err) {
                if (err) {
                    res.send({
                        doc: null,
                        err: err
                    });
                }

                res.send({
                    doc: round,
                    err: err
                });
            });
        });
    } else {
        return res.json({
            doc: null,
            err: "Not logged in"
        });
    }
};

exports.chooseQuestion = function (req, res) {
    if (req.session.username) {
        Round.update({_id: req.body.roundId}, {$push: {questions: req.body.questionId}}, {upsert: true}, function (err) {
            if (err) {
                res.send({
                    doc: null,
                    err: err
                });
            }
            res.send({
                doc: true,
                err: err
            });
        });
    } else {
        return res.json({
            doc: null,
            err: "Not logged in"
        });
    }
};