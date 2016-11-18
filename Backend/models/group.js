var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var groupSchema = new Schema({
    name: {type: String},
    score: {type: Number},
    roundpoints: {type: Number}
});

var Group = mongoose.model('Group', groupSchema, 'groups');

module.exports = Group;