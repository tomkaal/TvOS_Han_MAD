/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    Team = mongoose.model('Team');

exports.createOne = function (req, res) {
    var doc = new Team(req.body);

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
