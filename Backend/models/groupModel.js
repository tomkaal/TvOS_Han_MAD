var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var groupSchema = new Schema({
    name: {type: String},
    owner: {type: Schema.Types.ObjectId, ref: 'User'}
});

var Group = mongoose.model('Group', groupSchema, 'groups');

module.exports = Group;