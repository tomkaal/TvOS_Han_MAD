var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var teamSchema = new Schema({
    name: {type: String},
    group: {type: Schema.Types.ObjectId, ref: 'Group'},
    questions: [{question: {type: Schema.Types.ObjectId, ref: 'Question'}, correct: Boolean}]
});

var Team = mongoose.model('Team', teamSchema, 'teams');

module.exports = Team;