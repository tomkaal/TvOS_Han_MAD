/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    Group = mongoose.model('Group');

exports.createOne = function (req, res) {
    var doc = new Group(req.body);

    doc.save(function (err) {
        if (err) {
            return res.json({
                doc: null,
                err: err
            });
        }

        return res.json({
            doc: doc,
            err: err
        });
    });
};

exports.retrieveAll = function (req, res) {
    Group.find(function (err, doc) {
        if (err) {
            return res.json(
                {
                    doc: null,
                    err: err
                });
        }

        return res.json({
            doc: doc,
            err: err
        });
    });
};