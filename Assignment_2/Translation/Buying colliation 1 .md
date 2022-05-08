Here is a small example for my thinking about the buyer colliation problem.

# 1. Background

Assuming 10 items from a seller is on sale

1. each person can buy at most 5 of them,
2. for each item, the more buyer, the lower will the price be 

# 2.. Sets

1. 10 item -> $I=\{i_1,...i_{10}\}$

2. 10 Agents -> $A=\{A_1,...,A_{10}\}$

3. bids: each agent have a bid in every cycle, which contain less or equal to 5 items with the expected price for each of them (use letter c).

4. price curve of seller: e.g. for item 1, $N_{1j}->p_{1j}$ , where N represents the number of buyer of this item

# 3. Phases

## 3.1. Bid phase

for example
$$
A_1 \longrightarrow [(i_1,c_{1,1}),...,(i_5,c_{1,5})]\\
\vdots \\
A_{10} \longrightarrow [(i_3,c_{10,3}),...,(i_9,c_{10,9})] 
$$

## 3.2. Voting

for example, for agent 5 ($A_5$), he is considering the bid of agent 1 ($A_1$), he will calculate the expected value he will obtain from agent 1 's bid:
$$
\sum_{i \in item} w_{5,i} u_{5,i,j} \\
\text{first w means the weight of item i for agent 5, u means the utility for price interval of j for item i of agent 5}
$$


so how can we obtain $u_{5,i,j}$, the following is the most fundamental way, but it is quite similar with the human thought, for example, if $i_1$ is in the bid of $A_1$, then for item 1:

1. Agent 5 calculate how many other people has item 1 in their bids $\rightarrow$ which implies how many people may have high motivation to buy item 1, for example this number now is $n_1$
2. so he will find the price of item 1 from seller for $(n_1+1)$ buyer, and that is his initial evaluation of the price of item 1.
3. (optional) check whether price from 2 is suitable for other people who has item 1 in their bids, and update this price by some ways

Doing this price for all items in the bid of agent 1and then, agent 5 will have a expected value, namely $e\_value_{5,1}$ and then compare it to his reservation expected value $\alpha_{5}$:

1. if  $e\_value_{5,1} < \alpha_5$, reject this bid;
2. if   $e\_value_{5,1}$  slightly larger than $\alpha_5$, then accept it, but with high $C_{min}$ and low $C_{max}$, which means small possibility for accepting
3. if  $e\_value_{5,1}$ greatly larger than $\alpha_5$, then agent 5 accept it without possibility or just with low $C_{min}$ and high $C_{max}$, which means large possibility for accepting

## 3.3. Opt-in

for example for agent 5, now he knows other agents' altitude for each bid, and now he may use this data update his evaluation about the number of agents who has high motivation to buy item 1, (for example, if other agents have accept or high possibility for accepting a bid which has item 1, that means he has high motivation, vice versa):

1. if the number become greater, then holds $C_{min}$ and may make $C_{max}$ larger to extend the possibility
2. if the number become smaller, then it can keep make $C_{min}$ higher





P.S. part of the process is quite corresponding for human behaviour and some is still not so appropriate in the daily life, but I do not have more idea to improve it .

