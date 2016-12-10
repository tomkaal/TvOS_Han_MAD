/*jslint node: true */
"use strict";

var mongoose = require('mongoose'),
    Quiz = mongoose.model('Quiz'),
    Question = mongoose.model('Question'),
    Answer = mongoose.model('Answer'),
    asyncEach = require('async/each'),
    asyncEachSeries = require('async/eachSeries');


function sendErr(res, err) {
    return res.json({
        doc: null,
        err: err
    });
}

/* JSON example that this route expects (First answer in the answer arrays should be the correctAnswer)
 [
     {
         "tv": "584c416cf45827286c56e446",
         "quizdesc": "Bierbrouwerij",
         "questions": [
             {
                 "text": "Is bier lekker?",
                 "score": 3,
                 "answers": [
                     "Ja",
                     "Misschien",
                     "Nee",
                     "Rum is beter"
                 ]
             },
             {
                 "text": "Is bier gezond?",
                 "score": 2,
                 "answers": [
                     "Natuurlijk",
                     "Misschien",
                     "Nee",
                     "Rum is beter"
                 ]
             }
         ]
     },
     {
         "tv": "584c416cf45827286c56e446",
         "quizdesc": "Kaasmaker",
         "questions": [
             {
                 "text": "Is kaas lekker?",
                 "score": 4,
                 "answers": [
                     "Altijd",
                     "Misschien",
                     "Nee",
                     "Worst is beter"
                 ]
             }
         ]
     }
 ]
 */

// Controller function that will be used to fill the database with quizzes
exports.createQuizzes = function (req, res) {
    var quizzes = req.body;

    //iterate all quizzes with asynceach to make sure we can give a response to the route user when everything is done
    asyncEach(quizzes,
        function(quiz, quizCallback) {
            var newQuiz = new Quiz({
                tv: quiz.tv,
                description: quiz.quizdesc
            });

            newQuiz.save(function(err) {
                if (err) { return sendErr(res, err); }
                //iterate all questions from a quiz with asynceach to make sure we can give a response to the route user when everything is done
                asyncEach(quiz.questions,
                    function(question, questionCallback) {
                        var newQuestion = new Question({
                            quiz: newQuiz._id,
                            text: question.text,
                            score: question.score
                        });

                        newQuestion.save(function(err) {
                            if (err) { return sendErr(res, err); }

                            //array that holds the answerIds, the first one in the array will be the correctAnswer
                            //as the given json body should give an array of answers in a question and the first answer should be the correct answer
                            var answerIds = [];

                            //asyncEachSeries is used now so each async operation is done one at a time (asyncEach does all async operation parallel)
                            //this is done to ensure that the first answerId pushed to answerIds is the correctAnswer
                            asyncEachSeries(question.answers,
                                function(answer, answerCallback) {
                                    var newAnswer = new Answer({
                                        text: answer,
                                        question: newQuestion._id
                                    });

                                    newAnswer.save(function(err) {
                                        if (err) { return sendErr(res, err); }

                                        //push the saved answerId to the answerIds array
                                        answerIds.push(newAnswer._id);
                                        //calling the answerCallback to communicate that this answer iteration is done
                                        answerCallback();
                                    });
                                },
                                function(err) {
                                    if (err) { return sendErr(res, err); }

                                    newQuestion.correctAnswer = answerIds.shift();
                                    newQuestion.answers = answerIds;
                                    newQuestion.save(function(err){
                                        if (err) { return sendErr(res, err); }
                                        //calling the questionCallback to communicate that this question iteration is done
                                        questionCallback();
                                    });
                                }
                            );
                        });
                    },
                    function(err){
                        if (err) { return sendErr(res, err); }
                        //calling the quizCallback to communicate that this quiz iteration is done
                        quizCallback();
                    }
                );
            });
        },
        function(err) {
            if (err) { return sendErr(res, err); }
            //All quizzes with data are added, give response
            return res.json({
                doc: quizzes,
                err: null
            });
        }
    );
};