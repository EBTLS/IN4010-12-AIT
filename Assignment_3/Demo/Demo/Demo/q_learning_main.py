import simple_grid
from q_learning_skeleton import *
from q_learning_skeleton_2 import *
import gym 
from gridPlot import plot_policy

import matplotlib.pyplot as plt 

SUM_THREAT=0.01
MAX_THREAT=0.01

FLAG1_THREAT=5
FLAG2_THREAT=5

def act_loop(env, agent, num_episodes):
    
    flag1=0
    flag2=0
    
    for episode in range(num_episodes):
    
        difference_sum=0
        
        state = env.reset()

        print('---episode %d---' % episode)
        renderit = False
        if episode % 10 == 0:
            renderit = True

        for t in range(MAX_EPISODE_LENGTH):
            if renderit:
                env.render()
            printing=False
            if t % 500 == 499:
                printing = True

            if printing:
                print('---stage %d---' % t)
                agent.report()
                print("state:", state)

            action = agent.select_action(state,EPSILON)
            new_state, reward, done, info = env.step(action)
            if printing:
                print("act:", action)
                print("reward=%s" % reward)

            agent.process_experience(state, action, new_state, reward, done)
            state = new_state
            if done:
                print("Episode finished after {} timesteps".format(t+1))
                env.render()
                agent.report()
                break


        difference_max=agent.calculate_max_difference()        
        difference_sum=agent.calculate_sum_difference()
        
        if (difference_sum<SUM_THREAT):
            flag1=flag1+1
            
        if (difference_max<MAX_THREAT):
            flag2=flag2+1
                    
            
        # if (flag1>=FLAG1_THREAT) and (flag2>=FLAG2_THREAT):
            
        #     x=range(1,len(agent.difference_sum_history)+1,1)
            
        #     plt.plot(x,agent.difference_sum_history)
            
        #     plt.title("sum_history")
            
        #     plt.show()
        
        #     plt.plot(x,agent.difference_max_history)
            
        #     plt.title("max_history")
            
        #     plt.show()
        
        #     break
    
    print(flag1)
    
    print(flag2)
    
    if (flag1<FLAG1_THREAT) or (flag2<FLAG2_THREAT):
    
        x=range(1,len(agent.difference_sum_history)+1,1)
        
        plt.plot(x,agent.difference_sum_history)
        
        plt.title("sum_history")
        
        plt.show()
    
        plt.plot(x,agent.difference_max_history)
        
        plt.title("max_history")
        
        plt.show()

    env.close()
    



if __name__ == "__main__":
    # If you want to change the reward and penalty of environment, modify constants at simple_grid.py
    # If you want to run the walkInThePark, uncomment the next line and commnet the line behind it. 
    
    # env = simple_grid.DrunkenWalkEnv(map_name="walkInThePark")
    env = simple_grid.DrunkenWalkEnv(map_name="theAlley")
    num_a = env.action_space.n

    if (type(env.observation_space)  == gym.spaces.discrete.Discrete):
        num_o = env.observation_space.n
    else:
        raise("Qtable only works for discrete observations")


    discount = DEFAULT_DISCOUNT
    ql = QLearner(num_o, num_a, discount) #<- QTable
    act_loop(env, ql, NUM_EPISODES)
    
    # plot_policy("walkInThePark",ql.policy)
    
    
    
    ql_2 = QLearner_2(num_o, num_a, discount) #<- QTable
    act_loop(env, ql_2, NUM_EPISODES)
    
    x=range(1,len(ql.difference_sum_history)+1,1)
    plt.plot(x,ql.max_history,'r')
    
    x=range(1,len(ql_2.difference_sum_history)+1,1)
    plt.plot(x,ql_2.max_history,'b')
    
    print("best_policy_q_1")
    print(ql.policy)
    
    print("best_policy_q_2")
    print(ql_2.policy)
    
    plt.show()

