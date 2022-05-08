# Model

[toc]

# 1. Background

Assuming 1 seller has 10 kinds of items, 10 agents and 10 buyers.

1. Each agents knows the buyers preference and affordable price
2. Agents has different weights on different items.
3. 

# 2. Sets



# 3. Phases

## 3.1. Bid phase

firstly, the agent generates a division plan of buyers.

1. in each group, the money of different items can be re-distributed
2. in each group, generates money from different buyers on the same item can be re-distributed
3. no interaction among different groups

Division plan $\rightarrow$ bid: A list of number of things to buy from buyers.



## 3.2. Vote

For each bid, for example, agent 5 to bid 1: directly calculate the expected value from bid 1, agent 5 will have a expected value, namely $e\_value_{5,1}$ and then compare it to his reservation expected value $\alpha_{5}$:

1. if  $e\_value_{5,1} < \alpha_5$, reject this bid;
2. if   $e\_value_{5,1}$  slightly larger than $\alpha_5$, then accept it, but with high $C_{min}$ and low $C_{max}$, which means small possibility for accepting
3. if  $e\_value_{5,1}$ greatly larger than $\alpha_5$, then agent 5 accept it without possibility or just with low $C_{min}$ and high $C_{max}$, which means large possibility for accepting

And the same time, the agent 5 will have a list to discuss each agent's weight list for different items:

1. Most directly, each agent may try their best to increase the actual buyer from the potential buyer of their own most important things. (Calculate the ratio and guess the first weight)
2. Each agent may try their best to maximize the buyer of the item that will take the best utility to him
3. Use above to with weights.

## 3.3. Opt-in

After knowing the attitude from different agents of different items, then update the list in 3.2:

1. For example, if on agent 1's weight lists, item 5 has the 4th highest utility, he may make great number of item 5 in his bid, but reject/keep low probability of others bid with more item 5. Then we just find his altitude to 5 bids with most item 5, and calculate his preference, adjust our guessing list



Re-vote each bids:

1. if a bid has great possibility to be accepted, but actually not so good for our agent. make $C_{min}$ higher.
2.  If 1 agent we firstly guessed with high preference of our item, but then find not, make $C_{min}$ higher for his bids, (give him pressure and his bid may not very conform to our utiliy)

## 3.4. Re-bid

Based on the guessing list, "make the most power one of our defender agree our bids" or "try to make the defender whose most preference item(we have guess) is a good utility for us agree with us  "

