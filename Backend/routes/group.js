/*jslint node: true */

var express = require('express');
var router = express.Router();

var controller = require('../controllers/group');

router.route('/team/validate')
    .post(controller.validate);

router.route('/team/accept')
    .post(controller.accept);

module.exports = router;
