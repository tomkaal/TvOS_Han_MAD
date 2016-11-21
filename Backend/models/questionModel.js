var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var questionSchema = new Schema({
    quiz: {type: Schema.Types.ObjectId, ref: 'Quiz'},
    answers: [{type: Schema.Types.ObjectId, ref: 'Answer'}],
    correctAnswer: {type: Schema.Types.ObjectId, ref: 'Answer'},
    text: {type: String},
    score: {type: Number}
});

var Question = mongoose.model('Question', questionSchema, 'questions');

module.exports = Question;