import urllib2
import json
import re

exists = set()
conflicts = set()
sanereg = re.compile(r'[^\w.\- ]')
keepRooms = ("STUDIO","KILLIAN","KRESGE")


def parse(term):
    f = urllib2.urlopen("http://coursews.mit.edu/coursews/?term=%s" % (term))
    items = json.load(f)['items']
    classes = {x['id']:x['label'] for x in items if x['type'] == 'Class'}
    items = [{'id':x['section-of'],'term':term,'name':classes[x['section-of']],'timeAndPlace':timeAndPlace(x['timeAndPlace'])} for x in items if x['type'] == 'LectureSession']
    return items
    
def timeAndPlace(locRoom):
    saneLoc = sanereg.sub('',locRoom).strip()
    parts = saneLoc.split(" ")
    time = " ".join(parts[:-1])
    place = parts[-1]
    return {"time": time, "place": place}

def getJSON(term="2012SP"):
    # Get data
    data = parse(term)
    # Format data
    classes = map(formatClass, data)
    # Resolve conflicts
    # Collect by class name, remove classes with bad rooms
    classesRes = []
    classMap = {}
    for i in range(len(classes)):
        if validPlace(classes[i]['room']):
            if classes[i]['room'] not in classMap:
                classMap[classes[i]['room']] = []
            classMap[classes[i]['room']].append(classes[i])
        else:
            print "Invalid place for %s: %s" % (classes[i]['id'], classes[i]['room'])
    # Resolve conflicts.
    for mitID in classMap:
        classesRes.extend(resolveConflicts(classMap[mitID]))
    return {'classes':classesRes}

def resolveConflicts(classes):
    # resolve by specificying 'room'
    resolution = []
    rooms = set()
    # FIX ME
    for i in range(len(classes)):
        #print classes[i]
        #print classes[i]['room']
        if classes[i]['room'] not in rooms:
            classes[i]["resolve"] = classes[i]['room']
            del classes[i]['time']
            resolution.append(classes[i])
            rooms.add(classes[i]['room'])
    if len(resolution) == 1:
        del resolution[0]['resolve']
    print "Resolve:",resolution
    return resolution

def formatClass(x):
     return  {'id':x['id'],'term':x['term'],'name':x['name'],\
              'time':x['timeAndPlace']['time'],'room':x['timeAndPlace']['place']}

def validPlace(place):
    return place in keepRooms or re.match("[\w]+-[\w]+",place)

def JSONToFile(data):
    f = open("classes.json","w")
    json.dump(data, f)
    f.close()

JSONToFile(getJSON())
