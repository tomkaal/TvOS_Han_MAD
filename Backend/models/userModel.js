var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var userSchema = new Schema({
    name: {type: String},
    team: {type: Schema.Types.ObjectId, ref: 'Team'},
    questions: [{_id:false, question: {type: Schema.Types.ObjectId, ref: 'Question'}, correct: Boolean}]
});

var User = mongoose.model('User', userSchema, 'users');

module.exports = User;