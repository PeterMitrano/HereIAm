from pymongo import MongoClient
from bson import ObjectId
from datetime import datetime

client = MongoClient()

database = client["hereiam"]

games = database["games"]

seekers = database["seekers"]


games.insert({
	"gamename" : "XCG4AS",
	"hideralias" : "peter",
	"location" : [40.0, 0.0]})


seekers.insert({
	"seekeralias" : "peter",
	"location" : [40.0, 0.0],
	"gamename" : "XCG4AS",
	"gameid" : ObjectId('5468359c654a96492fed9590')
	})



for seeker in seekers.find({"game":games.find({"_id":gamename})}):


	#print seeker['codename'], games.find_one({"_id":seeker['game']})['gamename']