import sys
def split(base,ext):
    f = open(base+"."+ext,"r")
    i = 1
    while True:
        s = f.read(1024)
        if len(s) is 0: break
        g = open(base+str(i)+"."+ext,"w")
        g.write(s)
        g.close()
        i+=1
    f.close()    

name = sys.argv[1].split(".")
base = ".".join(name[:-1])
ext = name[-1]

split(base,ext)
