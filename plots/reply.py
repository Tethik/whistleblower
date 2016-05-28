import matplotlib.pyplot as plt
import matplotlib.lines as mlines

js = [5, 20, 100]
d = 1


def storage(d, w):
    return d * w

def bandwidth(j, d, w):
    return j * f * storage(d, w) * 8

accepted = [0.5, 0.75, 0.99]

def supported(p, f):
    return f * (1 - p)

# lines = []
#
# for j in js:
#     x = []
#     s = []
#     b = []
#     for f in range(0, 240, 1):
#         # week = f*24*7
#         x.append(f)
#         s = f / 60 / 60
#         w = supported(0.5, s)
#         b.append(bandwidth(j, d, w))
#
#     line = plt.plot(x, b, label=str(j))
#
# plt.legend()
# # [plt.plot(line) for line in lines]
# # plt.axis([0, 6, 0, 20])
# plt.xlabel('Avg rate of dummies (hour)')
# plt.ylabel('Bandwidth mb (second)')
# # plt.title('')
# plt.show()

x = []
s = []
for f in range(4, 240, 16):
    week = f*24*7
    x.append(f)
    w = supported(0.5, week)
    s.append(storage(d, w))


line = plt.plot(x, s)
plt.legend()
# [plt.plot(line) for line in lines]
# plt.axis([0, 6, 0, 20])
plt.xlabel('Avg rate of dummies (hour)')
plt.ylabel('Storage MB (week)')
# plt.title('')
plt.show()
