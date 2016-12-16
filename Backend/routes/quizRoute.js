var express = require('express');
var router = express.Router();

var controller = require('../controllers/quizController');

 router.route('/quiz')
     .post(controller.createQuizzes);

router.route('/quiz/:teamId/:questionId')
    .get(controller.getTeamScore);

module.exports = router;