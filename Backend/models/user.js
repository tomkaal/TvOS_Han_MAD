var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var userSchema = new Schema({
    name: {type: String},
    team: {type: Schema.Types.ObjectId, ref: 'Team'}
});

var User = mongoose.model('User', userSchema, 'users');

module.exports = User;