/*jslint node: true */

var express = require('express');
var router = express.Router();

var controller = require('../controllers/teamController');

router.route('/team')
    .post(controller.createTeams);

router.route('/team/accept')
    .post(controller.accept);

module.exports = router;
