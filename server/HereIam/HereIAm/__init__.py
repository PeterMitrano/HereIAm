from flask import Flask,request
from pymongo import MongoClient
from bson import ObjectId

client = MongoClient()

database = client["hereiam"]
games = database["games"]
seekers = database["seekers"]

app = Flask(__name__)

@app.route("/hereiam/get_hider_location", methods=['GET'])
def get_hider_location():
    seekerAlias = request.args.get("seekerAlias")
    the_seeker = seekers.find_one({"seekerAlias":seekerAlias})
    game_id = the_seeker["game_id"]
    the_game = games.find_one({"_id":game_id})
    location = the_game["location"]
    return str(location)

@app.route("/hereiam/get_seeker_locations", methods=['GET'])
def get_seeker_locations():
    hiderAlias = request.args.get("hiderAlias")
    game_id = games.find_one({"hiderAlias":hiderAlias})["_id"]
    locations = seekers.find({"gameId":game_id})["seekerLocation"]
    return str(locations)
    

@app.route("/hereiam/start_game", methods=['POST','GET'])
def start_game():
    gameName = request.args.get('gameName')
    hiderAlias = request.args.get('hiderAlias')
    writeResult = games.insert({
        "gameName": gameName,
        "hiderAlias": hiderAlias })
    gameId = games.find_one({"gameName":gameName})["_id"]
    seekers.update({"gameName":gameName},{"$set":{"gameId":gameId}},multi=True)
    return writeResult['nInserted']
    
@app.route("/hereiam/join_game", methods=['POST','GET'])
def join_game():
    gameName = request.args.get('gameName')
    seekerAlias = request.args.get('seekerAlias')
    writeResult = seekers.insert({
        "seekerAlias": seekerAlias,
        "gameName": gameName })
    return writeResult['nInserted']

@app.route("/hereiam/end_game", methods=['POST','GET'])
def end_game():
    hiderAlias = request.args.get("hiderAlias")
    the_game = games.update({"hiderAlias":hiderAlias},{"$set":{"gameState":"invalid"}})

@app.route("/hereiam/publish_seeker_location", methods=['POST','GET'])
def publish_seeker_location():
    seekerAlias = request.args.get('seekerAlias')
    seekerLocation = [float(i) for i in request.args.get('seekerLocation').split(",")]
    seekers.update({"seekerAlias":seekerAlias},{"$set":{"seekerLocation":seekerLocation}})
    
@app.route("/hereiam/publish_hider_location", methods=['POST','GET'])
def pubish_hider_location():
    hiderAlias = request.args.get('hiderAlias')
    hiderLocation = [float(i) for i in request.args.get('hiderLocation').split(",")]
    games.update({"hiderAlias":hiderAlias},{"$set":{"hiderLocation":hiderLocation}})

if __name__ == "__main__":
    app.run()
