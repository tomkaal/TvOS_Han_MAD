/*jslint node: true */

var express = require('express');
var router = express.Router();

var controller = require('../controllers/tvController');

router.route('/tv/:beaconId')
    .get(controller.retrieveOne);

router.route('/tv')
    .post(controller.createOne);

module.exports = router;
