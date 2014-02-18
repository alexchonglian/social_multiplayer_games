size = 3

for i in range(size):
	print zip (range(i, -size, -1),  [size-1-i]*(size+i) , range(-(size-1), 1+i) )
for i in range(size - 1):
	print zip (range((size-1), -size+1+i, -1),  [-1-i]*(2*size-2-i) , range(-size+2+i, size) )
