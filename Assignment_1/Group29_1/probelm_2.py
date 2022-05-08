# -*- coding: UTF-8 -*-

from Bayes import Bayes

if __name__ =='__main__':
    # 3. THE ARCHERY PROBLEM
    hypos = ["Beginner", "Intermediate", "Advanced", "Expert"]
    priors = [0.25, 0.25, 0.25, 0.25]
    obs = ["yellow", "red", "blue", "black", "white"]
    likelihood = [
        [0.05, 0.1, 0.4, 0.25, 0.2],
        [0.1, 0.2, 0.4, 0.2, 0.1],
        [0.2, 0.4, 0.25, 0.1, 0.05],
        [0.3, 0.5, 0.125, 0.05, 0.025]
    ]
    b_2 = Bayes(hypos=hypos, priors=priors, obs=obs, likelihood=likelihood)
    p = b_2.compute_posterior(["yellow", "white", "blue", "red", "red", "blue"])
    print("posterior: ", p)
