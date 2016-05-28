import matplotlib.pyplot as plt
import matplotlib.lines as mlines

js = [5, 20, 100]
d = 1


def storage(d, f):
    return d * f

def bandwidth(j, d, f):
    return j * storage(d, f) * 8 #8bit = 1 byte



# lines = []
#
# for j in js:
#     x = []
#     s = []
#     b = []
#     for f in range(4, 240, 16):
#         s = f / 60 / 60
#         x.append(f)
#         b.append(bandwidth(j, d, s))
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
    w = f*24*7
    x.append(f)
    s.append(storage(d, w))


line = plt.plot(x, s)
plt.legend()
# [plt.plot(line) for line in lines]
# plt.axis([0, 6, 0, 20])
plt.xlabel('Avg rate of dummies (hour)')
plt.ylabel('Storage MB (week)')
# plt.title('')
plt.show()
