size = 5
n = size - 1

points = [(x, y, z) for x in range(1-size, size) 
					for y in range(1-size, size)
					for z in range(1-size, size) if sum((x,y,z)) == 0]



def distance(p, q):
	return sum([abs(c1 - c2) for c1, c2 in zip(p, q)])

def is_neighbor(p, q):
	#print 'p in points ', p in points
	#print 'q in points ', q in points
	#print 'distance(p, q) ', distance(p, q)
	return distance(p, q) == 2

def neighbors_of(p):
	x,y,z = p
	return [q for q in [(x,y+1, z-1),(x,y-1,z+1),(x+1,y,z-1),(x-1,y,z+1),(x+1,y-1, z),(x-1,y+1, z)]
		if (size not in q) and (-size not in q)]
	#return [q for q in points if is_neighbor(p, q)]

def is_corner(p):
	return n in p and -n in p

def is_side(p):
	return (n in p) ^ (-n in p)

def is_valid(p):
	return p in points



"""
print len(points)
#>>> 61
print 3*size**2 - 3*size + 1
#>>> 61
print distance((0,0,0), (1,0,-1))
#>>> 2
print is_neighbor((0,0,0), (1,0,-1))
#>>> True
print neighbors_of((4, 0, -4))
print neighbors_of((2, 2, -4))
print neighbors_of((0, 0, -0))

print is_corner((4, -4, -0))
print is_corner((4, 0, -4))
print is_corner((0, 4, -4))
print is_side((4, -3, -1))
print is_side((2, -4, 2))
print is_side((-1, -3, 4))
#>>> True
"""


c1 = 'c1'

B = {(-1, 0, 1):c1,  (-1, 1, 0):c1,  (0, -1, 1):c1,  (0, 0, 0):c1,  (0, 1, -1):c1,  (1, -1, 0):c1}

newpt = (1, 0, -1)

stack = []

print B.get((5,0,0))==None



