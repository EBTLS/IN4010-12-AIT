class Bayes(object):
    def __init__(self, **kwargs):
        # sanity check for length of flwin' list params

        self.hypos = kwargs['hypos']
        self.obs = kwargs['obs']
        self.priors = kwargs['priors'] 
        self.likelihood_list = kwargs['likelihood']

        # self.norm_const = calc_norm_const()
        # self.posterior = calc_posterior()

    def likelihood(self, ob, hypo):
        return self.likelihood_list[self.hypos.index(hypo)][self.obs.index(ob)]

    def norm_constant(self, ob):
        norm_const = 0
        for i, h in enumerate(self.hypos):
            norm_const += self.priors[i] * self.likelihood(ob, h)
        return norm_const

    def single_posterior_update(self, ob, new_priors):
        self.priors = new_priors
        post = {}
        for i, h in enumerate(self.hypos):
            post[h] = round(
                            self.priors[i] * self.likelihood(ob, h) / self.norm_constant(ob),
                            3)
        return post

    def compute_posterior(self, obs):
        posterior = {}
        # denominator
        overall_norm_const = 0
        for i, h in enumerate(self.hypos):
            norm_const = self.priors[i]
            for ob in obs:
                norm_const *= self.likelihood(ob, h)
            overall_norm_const += norm_const
        # print("overall_norm_const: ", overall_norm_const)

        for i, h in enumerate(self.hypos):
            # numerator
            likelihood = self.priors[i]
            for ob in obs:
                likelihood *= self.likelihood(ob, h)
            posterior[h] = round( likelihood / overall_norm_const, 3 )
            # posterior[h] = likelihood / overall_norm_const 
        return posterior
    
if __name__ == '__main__':
    # 2. THE COOKIE PROBLEM

    hypos = ["Bowl1", "Bowl2"]
    priors = [0.5, 0.5]
    obs = ["chocolate", "vanilla"]
    # e.g. likelihood[0][1] corresponds to the likehood of Bowl1 and vanilla, or 35/50
    likelihood = [[15/50, 35/50], [30/50, 20/50]]

    b = Bayes(hypos=hypos, priors=priors, obs=obs, likelihood=likelihood)

    l = b.likelihood("chocolate", "Bowl1")
    print("likelihood(chocolate, Bowl1) = %s " % l)

    n_c = b.norm_constant("vanilla")
    print("normalizing constant for vanilla: %s" % n_c)

    p_1 = b.single_posterior_update("vanilla", [0.5, 0.5])
    print("vanilla - posterior: %s" % p_1)

    p_2 = b.compute_posterior(["chocolate", "vanilla"])
    print("chocolate, vanilla - posterior: %s" % p_2)


