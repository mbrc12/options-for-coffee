package org.mbrc.optionsforcoffee;

public class GeometricBrownianMotion {

    /* The GBM expression used is :

        S(t) = S(0) exp( (mu - sigma^2 / 2) t + sigma * W(t) )

        where S(t) is the GBM and W(t) is the standard brownian motion,
        where W(t) ~ N(0, t). S(0) is represented here as start.
     */

    private double sigma;
    private double mu;
    private double start;

    GeometricBrownianMotion() {     // Create some random GBM
        this.sigma = 0.01;
        this.mu = 0;
        this.start = 100.0;
    }

    GeometricBrownianMotion(double mu, double sigma, double start) {  // Create GBM with specified parameters
        this.sigma = sigma;
        this.mu = mu;
        this.start = start;
    }

    public String toString() {
        return "{ sigma = " +  sigma +  ", mu = " +  mu +  ", start = " + start + "}";
    }


    public double getMu() {
        return this.mu;
    }

    public double getSigma() {
        return this.sigma;
    }

    public double getStart() {
        return this.start;
    }

    public LogNormalDistribution getDistribution(double t) {
        double mu = Math.log(start) + ((this.mu - (this.sigma * this.sigma / 2)) * t);
        double sigma = this.sigma * Math.sqrt(t);

        return new LogNormalDistribution(mu, sigma);
    }

    // Estimate GBM with data treating it as-if consecutive elements are separated by delta

    public static GeometricBrownianMotion estimate(MathArray data, double delta) {
        if (data.length() <= 1) {
            throw new RuntimeException("GBM cannot be estimated from 0 or 1 sized data");
        }

        if (delta < 0.0) {
            throw new RuntimeException("Time-step delta cannot be negative");
        }

        data.logi();

        long N = data.length();
        double start = data.get(0);

        data.diff();

        double sigma = data.std() * Math.sqrt(delta);
        double mu = data.mean() / delta + sigma * sigma / 2.0;

        return new GeometricBrownianMotion(mu, sigma, start);
    }


    public MathArray[] generateSamplePaths(int howMany, int steps) {

        double drift = this.mu - this.sigma * this.sigma / 2;

        MathArray[] S = new MathArray[howMany];

        for (int pos = 0; pos < howMany; pos++) {
            S[pos] = new MathArray(steps);
            S[pos].randn(1, steps);
            S[pos].muli(this.sigma);
            S[pos].addi(1, steps, drift);

            S[pos].cumsum();
            S[pos].expi();
            S[pos].muli(start);
        }

        return S;
    }


    // Deterministic generation of paths


    public MathArray[] generateSamplePaths(int howMany, int steps, long seed) {

        double drift = this.mu - this.sigma * this.sigma / 2;

        MathArray[] S = new MathArray[howMany];

        for (int pos = 0; pos < howMany; pos++) {
            S[pos] = new MathArray(steps);
            S[pos].randn(1, steps, seed);
            S[pos].muli(this.sigma);
            S[pos].addi(1, steps, drift);

            S[pos].cumsum();
            S[pos].expi();
            S[pos].muli(start);
        }

        return S;
    }
}
