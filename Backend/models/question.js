var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var questionSchema = new Schema({
    answers: [{text: {type: String}, correct: Boolean}],
    text: {type: String},
    quiz: {type: Schema.Types.ObjectId, ref: 'Quiz'},
    score: {type: Number}
});

var Question = mongoose.model('Question', questionSchema, 'Questions');

module.exports = Question;