/**
 * Created by Arthur on 21-Nov-16.
 */

var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var answerSchema = new Schema({
    text: {type: String},
    question: {type: Schema.Types.ObjectId, ref: 'Question'}
});

var Question = mongoose.model('Answer', answerSchema, 'answers');

module.exports = Question;