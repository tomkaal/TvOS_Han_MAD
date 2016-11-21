/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    Team = mongoose.model('Team'),
    asyncEach = require('async/each');

exports.createOne = function (req, res) {
    //var doc = new Team(req.body);
    var data = req.body;

    asyncEach(data.teams,
        function(team, callback) {
            //do stuff to add team to db and users to that team then call callback
        },
        function(err) {
            //All teams are added and users update give response
        });

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
