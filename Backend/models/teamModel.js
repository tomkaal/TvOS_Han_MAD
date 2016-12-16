var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var teamSchema = new Schema({
    name: {type: String},
    group: {type: Schema.Types.ObjectId, ref: 'Group'},
    questions: [{
        _id:false,
        question: {type: Schema.Types.ObjectId, ref: 'Question'},
        answer: {type: Schema.Types.ObjectId, ref: 'Answer'}
    }]
});

var Team = mongoose.model('Team', teamSchema, 'teams');

module.exports = Team;