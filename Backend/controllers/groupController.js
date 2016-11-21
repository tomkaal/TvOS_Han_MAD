/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    Group = mongoose.model('Group');

exports.createOne = function (req, res) {
    var doc = new Group(req.body);

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