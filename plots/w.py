def w(j, p):
    return 4 * j * (1 - p)

for p in [0.5, 0.75, 0.99]:
    print([w(j, p)*24*7 for j in [5, 20, 50]])
