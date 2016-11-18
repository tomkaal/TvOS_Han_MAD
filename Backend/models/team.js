var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var teamSchema = new Schema({
    name: {type: String},
    score: {type: Number},
    roundpoints: {type: Number}
});

var Team = mongoose.model('Team', teamSchema, 'teams');

module.exports = Team;