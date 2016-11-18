/*jslint node: true */

var Team = require('../models/team');
var express = require('express');
var router = express.Router();

var controller = require('../controllers/team');

router.route('/team/validate')
    .post(controller.validate);

router.route('/team/accept')
    .post(controller.accept);

module.exports = router;
