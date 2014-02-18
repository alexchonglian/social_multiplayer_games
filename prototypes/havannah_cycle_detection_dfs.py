a = 'A'
b = 'B'
c = 'C'
d = 'D'
e = 'E'
f = 'F'
g = 'G'
h = 'H'



adj = { a:[d,c,b],
		b:[f,e,c,a],
		c:[h,g,f,d,b,a],
		d:[a,h,c],
		e:[f,b],
		f:[g,c,b,e],
		g:[h,c,f],
		h:[d,c,g]
		}


def is_neighbor(x,y):
	return y in adj[x]

nei = { a:[d,c,b],
		b:[f,e,c,a],
		c:[h,g,f,d,b,a],
		d:[a,h,c],
		e:[f,b],
		f:[g,c,b,e],
		g:[h,c,f],
		h:[d,c,g]
		}

stack = [a]

while stack != []:

	current = stack[-1]

	if nei[current] == []:#no neighbor then pop stack and try previous ones neighbors
		stack.pop()

	else:#current still has unused neighbors
		cand = nei[current].pop()#find candidate
		if len(stack) == 1:
			if cand != current: stack.append(cand)
		elif cand != current and cand != stack[-2] and (not is_neighbor(stack[-2], cand)):
			#make sure candidate is no neighbor with stack[-2]
			if cand in stack[:-2]: print stack+[cand]
			stack.append(cand)


#>>> ['A', 'B', 'F', 'G', 'H', 'D', 'A']









