/*jslint node: true */

var express = require('express');
var router = express.Router();

var controller = require('../controllers/group');

router.route('/group')
    .post(controller.createOne);

router.route('/team/accept')
    .post(controller.accept);

module.exports = router;
