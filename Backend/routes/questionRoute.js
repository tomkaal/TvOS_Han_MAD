var express = require('express');
var router = express.Router();

var controller = require('../controllers/questionController');

router.route('/question/answer/:answerId')
    .post(controller.answer);

module.exports = router;