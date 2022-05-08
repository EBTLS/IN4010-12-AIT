import matplotlib.pyplot as plt
from matplotlib.pyplot import MultipleLocator
fig = plt.figure()
ax = fig.add_subplot(111)
MAPS = {
    "theAlley": [
        "S...H...H...G"
    ],
    "walkInThePark": [
        "S.......",
        ".....H..",
        "........",
        "......H.",
        "........",
        "...H...G"
    ],
    "1Dtest": [

    ],
    "4x4": [
        "S...",
        ".H.H",
        "...H",
        "H..G"
    ],
    "8x8": [
        "S.......",
        "........",
        "...H....",
        ".....H..",
        "...H....",
        ".HH...H.",
        ".H..H.H.",
        "...H...G"
    ],
}

def plot_policy(mp_name,result):
    mp = MAPS[mp_name]
    length = len(mp)
    width = len(mp[0])
    for i in range(0,length):
    	for j in range(0,width):
    		yindex = length - 1 - i
    		if mp[yindex][j]=='H':
    			ax.add_patch(plt.Rectangle((j+0.05,i+0.05),0.9,0.9,color='grey', alpha=0.5))
    		elif mp[yindex][j]=='S':
    			ax.add_patch(plt.Rectangle((j+0.05,i+0.05),0.9,0.9,color='green', alpha=0.5))
    		elif mp[yindex][j]=='G':
    			ax.add_patch(plt.Rectangle((j+0.05,i+0.05),0.9,0.9,color='red', alpha=0.5))
    		if result[i*width+j]==0:
    			ax.arrow(j+1-0.25,yindex*1+0.5,-0.5,0,length_includes_head = True,head_width = 0.125,head_length = 0.25,fc = 'black',ec = 'black')
    		elif result[i*width+j]==1:
    			ax.arrow(j+0.5,yindex*1+1-0.25,0,-0.5,length_includes_head = True,head_width = 0.125,head_length = 0.25,fc = 'black',ec = 'black')
    		elif result[i*width+j]==2:
    			ax.arrow(j+0.5-0.25,yindex*1+0.5,0.5,0,length_includes_head = True,head_width = 0.125,head_length = 0.25,fc = 'black',ec = 'black')
    		elif result[i*width+j]==3:
    			ax.arrow(j+0.5,yindex*1+0.5-0.25,0,0.5,length_includes_head = True,head_width = 0.125,head_length = 0.25,fc = 'black',ec = 'black')
    x_major_locator=MultipleLocator(1)
    y_major_locator=MultipleLocator(1)
    ax.xaxis.set_major_locator(x_major_locator)
    ax.yaxis.set_major_locator(y_major_locator)
    ax.set_xlim(0,width)
    ax.set_ylim(0,length)
    ax.grid()  #添加网格
    ax.set_aspect('equal')  #x轴和y轴等比例
    plt.show()
    plt.tight_layout()