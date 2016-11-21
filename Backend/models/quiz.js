var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var quizSchema = new Schema({
    tv: {type: Schema.Types.ObjectId, ref: 'Tv'},
    description: {type: String}
});

var Quiz = mongoose.model('Quiz', quizSchema, 'quizzes');

module.exports = Quiz;