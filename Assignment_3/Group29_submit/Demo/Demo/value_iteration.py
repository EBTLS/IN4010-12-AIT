# -*- coding: utf-8 -*-
"""
Created on Sat Dec 26 16:02:52 2020

@author: xuan
"""

NUM_EPISODES = 1000

DEFAULT_DISCOUNT = 0.9

ACTION_ACCURACY=0.8


LEFT = 0
DOWN = 1
RIGHT = 2
UP = 3

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
# If you want to change reward and penalty, modify these constants
POTHOLE_PROB = 0.2
BROKEN_LEG_PENALTY = -10
SLEEP_DEPRIVATION_PENALTY = -0.0
REWARD = 10


import numpy as np
from gridPlot import plot_policy


class dynamic_value_iteration():
    
    
    def __init__(self,map_name, num_actions, discount=DEFAULT_DISCOUNT,action_prob=ACTION_ACCURACY):
        
        self.env_map=MAPS[map_name]
        self.nrow=len(self.env_map)
        print(self.nrow)
        self.ncol=len(self.env_map[0])
        print(self.ncol)
        self.states_num=self.nrow*self.ncol
        self.reward_initialize()
        
        
        self.actions_num=num_actions
        self.discount=discount
        self.action_accuracy=action_prob
        
        self.value_table=np.zeros((self.states_num,self.actions_num))
        self.policy=[];
        
        
        
    def reward_initialize(self):
        '''
        initialize of the reward map
        '''
        
        self.reward_map=np.zeros([self.nrow,self.ncol])
        
        for i in range (0,self.nrow):
            for j in range (0,self.ncol):
                
                if self.env_map[i][j]=="H":
                
                    self.reward_map[i][j]=BROKEN_LEG_PENALTY*POTHOLE_PROB
                    
                elif self.env_map[i][j]=="G":
                    
                     self.reward_map[i][j]=REWARD
                    
                else:
                    self.reward_map[i][j]=0
        
        print(self.reward_map)
        
                    
                    
        
    
    def value_update(self,row,col,action):
        
        '''
        value update for (s,a)
    
        '''
        
        state=self.ncol*row+col;
        
        
        optimal_value=self.calculate_next_utility(row,col,action)
        
        # print(optimal_value)
        
        self.value_table[state][action]=self.reward_map[row][col]+self.discount*optimal_value
        
        # print("("+str(row)+","+str(col)+","+str(action)+")")
        
        # print(self.value_table[state][action])
        
        # input()
        
        
        pass
    
    def calculate_next_utility(self,row,col,action):
        
        '''
        calculate the expectation utility of next state
        '''

        temp_value=0 
        
        next_row,next_col=self.find_next_state(row, col, action)
        
        next_state=self.ncol*next_row+next_col
        
        # print("next_state:")
        # print(next_state)
        
        temp_value=temp_value+self.action_accuracy*max(self.value_table[next_state])
        
        # print(temp_value)
        
        next_row,next_col=self.find_next_state(row, col, (action+1)%4)
        
        next_state=self.ncol*next_row+next_col
        
                
        # print("next_state:")
        # print(next_state)
        
        temp_value=temp_value+(1-self.action_accuracy)/2*max(self.value_table[next_state])
        
                
        # print(temp_value)
        
        next_row,next_col=self.find_next_state(row, col, (action+3)%4)
        
        next_state=self.ncol*next_row+next_col
        
                
        # print("next_state:")
        # print(next_state)
        
        temp_value=temp_value+(1-self.action_accuracy)/2*max(self.value_table[next_state])
        
                
        # print(temp_value)
        
        # input()
        
        return temp_value
        
    
    
    def find_next_state(self,row,col,action):
        
        
        
        '''
        based on action find the next state (row, col)
        '''
        
        if action==LEFT:
            
            col=max(col-1,0)
            row=row
            
        elif action==DOWN:
            
            col=col;
            row = min(row+1,self.nrow-1)
            
        
            
        elif action==RIGHT:
            
            col = min(col+1,self.ncol-1)
            row=row;
            
        
        elif action==UP:
            
            col=col
            row = max(row-1,0)
            
            
        # if self.env_map[row][col]=='H':
        #     col=0
        #     row=0
            
            
        return(row,col)
            
    
    def value_update_loop(self):
        
        '''
        main loop for DP
        '''
        
        for k in range(0,NUM_EPISODES):
            
        #     for i in range (self.nrow,0,-1):
        #         for j in range (self.ncol,0,-1):
        #             for a in range(0,self.actions_num,1):
                        
        #                 if (self.env_map[i-1][j-1]=="H"):
        #                    self.value_table[self.ncol*(i-1)+j-1][a]=max(self.value_table[0])+BROKEN_LEG_PENALTY*POTHOLE_PROB
            
            
            for i in range (self.nrow,0,-1):
                for j in range (self.ncol,0,-1):
                    for a in range(0,self.actions_num,1):
                        
                        # print([i,j])
                        if (self.env_map[i-1][j-1]=="G"):
                            # print("yes")
                            self.value_table[self.states_num-1][a]=REWARD
                            # print(self.states_num)
                            # input()
                        # elif (self.env_map[i-1][j-1]=="H"):
                        #     self.value_table[self.ncol*(i-1)+j-1][a]=BROKEN_LEG_PENALTY*POTHOLE_PROB
                            pass
                            
                        else:
                            self.value_update(i-1,j-1,a)
                            
                        
            

            print("current_table:")
            print(self.value_table)
            
        self.policy=[]
        
        for i in range(0,self.states_num):
            optimal_action=np.argmax(self.value_table[i])
            
            self.policy.append(optimal_action)
            
        
        print("best_policy:")
        print(self.policy)
            


if __name__ == "__main__":

    # if you want to run the walkInThePark, uncomment the next line and commnet the line behind it.
    #DP1=dynamic_value_iteration("walkInThePark",4)
    DP1=dynamic_value_iteration("theAlley",4)
    DP1.value_update_loop()
    # if you want to run the walkInThePark, uncomment the next line and commnet the line behind it.
    #plot_policy("walkInThePark",DP1.policy)
    plot_policy("theAlley",DP1.policy)
                
        
        
        

    
        
        