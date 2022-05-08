# -*- coding: UTF-8 -*-

from Bayes_1 import Bayes

if __name__ =='__main__':
    hypos = ["beginner", "intermediate","advanced","expert"]
    priors = [0.25,0.25,0.25,0.25]
    obs = ["yellow","red","blue","black","white"]
    likelihood = [[0.05, 0.1,0.4,0.25,0.2], [0.1,0.2,0.4,0.2,0.1],[0.2,0.4,0.25,0.1,0.05],[0.3,0.5,0.125,0.05,0.025]]
    b = Bayes(hypos, priors, obs, likelihood)
    p_2 = b.compute_posterior(["yellow", "white","blue","red","red","blue"])
    print("yellow, white, blue, red, red, blue - posterior: %s" % p_2)