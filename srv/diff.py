import urllib2
import json
def parse(term="2012SP"):
    f = urllib2.urlopen("http://coursews.mit.edu/coursews/?term=%s" % (term))
    items = json.load(f)['items']
    classes = {x['id']:x['label'] for x in items if x['type'] == 'Class'}
    items = [{'id':x['section-of'],'name':classes[x['section-of']],'room':room(x['timeAndPlace'])} for x in items if x['type'] == 'LectureSession']
    return items
    
def room(locRoom):
    return locRoom.split(" ")[1]

def diff(timestamp="0"):
    # In later releases, should take timestamp into account.
    data = parse()
    result = {'diffs':map(lambda x: {'diff':'+','id':x['id'],'name':x['name'],'room':x['room']}, data)}
    return result

def jsonToFile(data):
    f = open("diff.json","w")
    json.dump(data, f)
    f.close()
