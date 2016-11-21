/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    User = mongoose.model('User');

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
