
NUM_EPISODES = 5000
MAX_EPISODE_LENGTH = 500

DEFAULT_DISCOUNT = 0.9
EPSILON = 0.05
LEARNINGRATE = 0.1

import numpy as np
import math
import random
import copy


class QLearner_2():
    """
    Q-learning agent
    """
    def __init__(self, num_states, num_actions, discount=DEFAULT_DISCOUNT, learning_rate=LEARNINGRATE): # You can add more arguments if you want
        self.name = "agent1"
        self.states_num=num_states
        self.actions_num=num_actions
        self.discount=discount
        self.learning_rate=learning_rate
        
        self.Q_table=np.zeros((self.states_num,self.actions_num))
        self.Q_table_prev=np.zeros((self.states_num,self.actions_num))
        self.difference_sum_history=[]
        self.difference_max_history=[]
        self.policy=[];
        
        self.max_history=[]
        

    def process_experience(self, state, action, next_state, reward, done): # You can add more arguments if you want
        """
        Update the Q-value based on the state, action, next state and reward.
        """
        
        temp_utility=self.Q_table[state][action]
        current_utility=np.max(self.Q_table_prev[next_state])
        
        self.Q_table[state][action]=self.Q_table_prev[state][action]+\
            self.learning_rate*(reward+self.discount*current_utility-temp_utility)
            

            
        # print("current_Q_table")
        # print(self.Q_table)
        
        # pass

    def select_action(self, state,epsilon): # You can add more arguments if you want
        """
        Returns an action, selected based on the current state
        """
        
        # temp_random=random.uniform(0,1)
        
        # if (temp_random>epsilon):
        #     # if several actions are all the largest one, choose on randomly
            
            
        #     optimal_action_list=[];
            
        #     max_value=np.max(self.Q_table[state])
            
        #     for i in range(0,self.actions_num):
                
        #         if (self.Q_table[state][i]==max_value):
                    
        #             optimal_action_list.append(i)
                    
        #     if (len(optimal_action_list)==1):
        #         action_selected=np.argmax(self.Q_table[state])
            
        #     else: 
        #         random_selection=random.randint(0,len(optimal_action_list)-1)
        #         action_selected=optimal_action_list[random_selection]
            
        # else:
        #     random_selection=random.randint(0, self.actions_num-1)
        #     action_selected=random_selection
        
            
        # return action_selected
        
        temp_random=random.uniform(0,1)
        p = {}
        sumq = 0.0
        limit = {}
        for i in range(0,self.actions_num):
            sumq+=math.exp(self.Q_table[state][i]/2)
        for i in range(0,self.actions_num):
            p[i]=math.exp(self.Q_table[state][i]/2)/sumq
            if i==0:
                limit[i]=(p[i])
            else:
                limit[i]=(p[i]+limit[i-1])
            if limit[i]>temp_random:
                action_selected = i
                break
        
        return action_selected

        
        # pass
    
    
    def find_policy(self):
        
        """
        find the optimal policy
        """
        
        self.policy=[]
        
        for i in range(0,self.states_num):
            optimal_action=np.argmax(self.Q_table[i])
            
            self.policy.append(optimal_action)


    def report(self):
        """
        Function to print useful information, printed during the main loop
        """
        print("---")
        print("current_Q_table")
        print(self.Q_table)
        print("best_policy")
        self.find_policy()
        print(self.policy)
        
    
    def calculate_sum_difference(self):
        ''''calculate'''
        
        difference_sum=0
        
        for i in range (0,self.states_num):
            for j in range (0,self.actions_num):
                difference=(self.Q_table[i][j]-self.Q_table_prev[i][j])**2
                difference_sum=difference_sum+difference
            
        self.difference_sum_history.append(difference_sum)
        
        self.Q_table_prev=copy.copy(self.Q_table)
        
        return difference_sum
    
    
    
    def calculate_max_difference(self):
        '''calculate the change of max value in each iteration'''
        
        difference_max=abs(self.find_martrix_max_value(self.Q_table)-self.find_martrix_max_value(self.Q_table_prev))
        
        self.difference_max_history.append(difference_max)
        
        self.max_history.append(self.find_martrix_max_value(self.Q_table))
        
        return difference_max
    
    
    
    def find_martrix_max_value(self,data_matrix):
        '''
        find max value in the matrix
        '''
        new_data=[]
        for i in range(len(data_matrix)):
            new_data.append(max(data_matrix[i]))

        return max(new_data)
        
        
        
        
