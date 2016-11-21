var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var triviaSchema = new Schema({
    tv: {type: Schema.Types.ObjectId, ref: 'Tv'},
    text: {type: String}
});

var Trivia = mongoose.model('Trivia', triviaSchema, 'trivias');

module.exports = Trivia;