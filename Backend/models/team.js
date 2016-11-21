var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var teamSchema = new Schema({
    name: {type: String},
    group: {type: Schema.Types.ObjectId, ref: 'Group'}
});

var Team = mongoose.model('Team', teamSchema, 'teams');

module.exports = Team;