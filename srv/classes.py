import urllib2
import json
def parse(term):
    f = urllib2.urlopen("http://coursews.mit.edu/coursews/?term=%s" % (term))
    items = json.load(f)['items']
    classes = {x['id']:x['label'] for x in items if x['type'] == 'Class'}
    items = [{'id':x['section-of'],'term':term,'name':classes[x['section-of']],'room':room(x['timeAndPlace'])} for x in items if x['type'] == 'LectureSession']
    return items
    
def room(locRoom):
    print locRoom.split(" ")[-1]
    return locRoom.split(" ")[-1]

def getJSON(term="2012SP"):
    data = parse(term)
    result = {'classes':map(lambda x: {'id':x['id'],'term':x['term'],'name':x['name'],'room':x['room']}, data)}
    return result

def JSONToFile(data):
    f = open("classes.json","w")
    json.dump(data, f)
    f.close()

JSONToFile(getJSON())
