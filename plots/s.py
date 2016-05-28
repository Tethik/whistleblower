
def s(j, w):
    return 12 * j * w + 10 * j

for j in [5, 20, 50]:
    for w in [100, 1000, 10000]:
        print(j, w, s(j, w / 24 / 7) * 24 * 7  / 1000)
