import matplotlib.pyplot as plt
import matplotlib.lines as mlines

accepted = [0.5, 0.75, 0.99]

def supported(p, f):
    return f * (1 - p) * 24

lines = []

for p in accepted:
    x = []
    s = []
    for f in range(0, 240, 1):
        x.append(f)
        s.append(supported(p, f))

    line = plt.plot(x, s, label=str(p))
    lines.append(line)

plt.legend()
# [plt.plot(line) for line in lines]
# plt.axis([0, 6, 0, 20])
plt.xlabel('Avg rate of dummies per hour')
plt.ylabel('Supported Whistleblowers per day')
# plt.title('')
plt.show()
