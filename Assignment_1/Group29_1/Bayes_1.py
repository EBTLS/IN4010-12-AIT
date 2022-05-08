# -*- coding: UTF-8 -*-

class Bayes:

    def __init__(self, hyposthesis, priors_possibilities, observations, likelihood_array):
        self.hypo_list = hyposthesis
        self.priors_poss = priors_possibilities
        self.possible_obs = observations
        self.like_array = likelihood_array

    def likelihood(self, observation, hypothesis):
        line = self.hypo_list.index(hypothesis)
        row = self.possible_obs.index(observation)
        return self.like_array[line][row]

    def norm_constant(self, observation):
        norm_const = 0
        row = self.possible_obs.index(observation)
        for i in range(0, len(self.hypo_list), 1):
            norm_const = norm_const + self.like_array[i][row] * self.priors_poss[i]
        return norm_const

    def single_posterior_update(self, observation, priors):
        norm_const = self.norm_constant(observation)
        post_poss = []
        for hypo in self.hypo_list:
            like = self.likelihood(observation, hypo)
            line = self.hypo_list.index(hypo)
            post_poss.append(priors[line] * like / norm_const)
        return post_poss

    # def compute_posterior(self,observations):
    #     # post_poss=[1 for i in range(len(self.hypo_list))]
    #     # if want to use interable ways to update posterior_poss, change the initial
    #     # value of post_poss to priors_poss
    #
    #     post_poss=[poss for poss in self.priors_poss]
    #     # print(id(post_poss))
    #     # print(id(self.priors_poss))
    #     for observation in observations:
    #         temp_post_poss=self.single_posterior_update(observation,self.priors_poss)
    #         print(self.priors_poss)
    #         print(temp_post_poss)
    #         for i in range(len(post_poss)):
    #             post_poss[i]=post_poss[i]*temp_post_poss[i]/self.priors_poss[i]
    #
    #     return post_poss

    def compute_posterior(self, observations):

        post_poss = [poss for poss in self.priors_poss]
        total_prob=0
        for i in range(0, len(self.hypo_list)):
            temp_total_prob = self.priors_poss[i]
            for observation in observations:
                temp_total_prob = temp_total_prob * self.single_posterior_update(observation, self.priors_poss)[i] / \
                                  self.priors_poss[i]
            total_prob = total_prob + temp_total_prob;
        for observation in observations:
            temp_post_poss = self.single_posterior_update(observation, self.priors_poss)
            for i in range(len(post_poss)):
                post_poss[i] = post_poss[i] * temp_post_poss[i] / self.priors_poss[i]

        for i in range(len(post_poss)):
            post_poss[i]=post_poss[i]/total_prob

        return post_poss


if __name__ == '__main__':
    hypos = ["Bowl1", "Bowl2"]
    priors = [0.5, 0.5]
    obs = ["chocolate", "vanilla"]
    # e.g. likelihood[0][1] corresponds to the likehood of Bowl1 and vanilla, or 35/50
    likelihood = [[15 / 50, 35 / 50], [30 / 50, 20 / 50]]
    b = Bayes(hypos, priors, obs, likelihood)
    l = b.likelihood("chocolate", "Bowl1")
    print("likelihood(chocolate, Bowl1) = %s " % l)
    n_c = b.norm_constant("vanilla")
    print("normalizing constant for vanilla: %s" % n_c)
    p_1 = b.single_posterior_update("vanilla", [0.5, 0.5])
    print("vanilla - posterior: %s" % p_1)
    p_2 = b.compute_posterior(["chocolate", "vanilla"])
    print("chocolate, vanilla - posterior: %s" % p_2)
    p_3 = b.compute_posterior(["vanilla", "chocolate"])
    print("chocolate, vanilla - posterior: %s" % p_3)
    p_4= b.single_posterior_update("chocolate", [0.5, 0.5])
    print("chocolate - posterior: %s" % p_4)
