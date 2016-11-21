/*jslint node: true */

var express = require('express');
var router = express.Router();

var controller = require('../controllers/userController');

router.route('/user')
    .post(controller.createOne);

router.route('/user/accept')
    .post(controller.accept);

module.exports = router;
