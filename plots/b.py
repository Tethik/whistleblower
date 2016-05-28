
def _w(j, p):
    return 4 * j * (1 - p)

def b(j, p):
    # w = _w(j, p)
    # s = (w + (w / (1 - p))) * 40 + w * 8
    # return s + (j * (w + (w / (1 - p)) * 40)) + (2 * (w / (1 - p)) * 8) + (w * 8)

    return (96*(j**2)) * (1 - p) + (80*(j**2)) + 16*j

for p in [0.5, 0.75, 0.99]:
    print([b(j,p)*24*7 for j in [5, 20, 50]])
    print([_w(j, p)*24*7 for j in [5, 20, 50]])
