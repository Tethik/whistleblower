

def transfer(j,w):
    t = 3*w*j + 2*5*(j**2) + 2*1*j
    return t

bytestobits = 8
weektohours = 24 * 7
for j in [5, 20, 50]:
    for w in [100, 1000, 10000]:
        print(j, w, transfer(j, w / 24 / 7) * weektohours / 1000 * bytestobits)

        
