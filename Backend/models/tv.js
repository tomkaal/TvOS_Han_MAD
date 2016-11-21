var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var tvSchema = new Schema({
    description: {type: String},
    beaconId: {type: String}
});

var Tv = mongoose.model('Tv', tvSchema, 'tvs');

module.exports = Tv;