/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    Tv = mongoose.model('Tv');

exports.retrieveOne = function (req, res) {
    Tv.find({beaconId: req.params.beaconId}, function (err, doc) {
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