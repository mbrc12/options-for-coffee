package org.mbrc.optionsforcoffee;

import java.util.function.Function;

public class LogNormalDistribution {

    /* The definition of Log-Normal distribution
        adopted here is this:

        X = exp(mu + sigma * Z)

        where Z is a standard normal variable.
     */

    final static double FALLOFF = 100;

    public double mu;
    public double sigma;

    LogNormalDistribution(double mu, double sigma) {
        this.mu = mu;
        this.sigma = sigma;
    }

    public double mean() {
        return Math.exp(mu + sigma * sigma / 2);
    }

    public double var() {
        return (Math.exp(sigma * sigma) - 1) * Math.exp(mu + mu - sigma * sigma);
    }

    public double std() {
        return Math.sqrt(var());
    }

    public double pdf(double t) {
        return (1.0/(t * sigma * Math.sqrt(Math.PI * 2))) *
                Math.exp(-Math.pow(Math.log(t) - mu, 2) / (2 * sigma * sigma));
    }

    public MathArray getSamples(int numberOfSamples) {

        MathArray samples = new MathArray(numberOfSamples);
        samples.randn();
        samples.muli(sigma);
        samples.addi(mu);
        samples.expi();

        return samples;
    }

    public double integrateOverDistribution(Function<Double, Double> function, double l, double r) {

        if (r < l) {
            throw new RuntimeException("Empty interval as r < l !");
        }

        if (l < 0) {
            throw new RuntimeException("Values for left interval has to be > 0, because Log-Normal distribution is undefined when t < 0.");
        }

        Function<Double, Double> f = (t) -> function.apply(t) * pdf(t);

        double mean = this.mean();
        double std = this.std();


        // Condition the ranges.

        l = Math.max(l, mean - LogNormalDistribution.FALLOFF * std);
        l = Math.max(l, Integrator.epsilon);
        r = Math.min(r, mean + LogNormalDistribution.FALLOFF * std);
        r = Math.min(r, Integrator.infinity);

        return Integrator.integrate(f, l, r);
    }
}
