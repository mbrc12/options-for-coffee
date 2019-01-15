package org.mbrc.optionsforcoffee;


import java.util.function.Function;

public class OptionPriceEstimator {

    /* This library only works for European Options */

//    final int SAMPLES = 20;

    private GeometricBrownianMotion stockPrice;
    private double riskFree;
    private double mu;
    private double sigma;

    OptionPriceEstimator(GeometricBrownianMotion stockPrice, double riskFree) {
        this.stockPrice = stockPrice;
        this.mu = stockPrice.getMu();
        this.sigma = stockPrice.getSigma();
        this.riskFree = riskFree;
    }

    // Uses the direct formula from https://en.wikipedia.org/wiki/Black%E2%80%93Scholes_model

    public double getEstimate(double spotPrice, double timeToMaturity, double strikePrice, OptionType optionType) {

        double d1 = Math.log(spotPrice / strikePrice) + (riskFree + sigma * sigma / 2) * timeToMaturity;
        d1 /= sigma * Math.sqrt(timeToMaturity);

        double d2 = d1 - sigma * Math.sqrt(timeToMaturity);

        double PV = strikePrice * Math.exp(-riskFree * timeToMaturity);

        double callPrice = CNDF(d1) * spotPrice - CNDF(d2) * PV;

        if (optionType == OptionType.CALL) return callPrice;

        double putPrice = PV - spotPrice - callPrice;

        return putPrice;
    }

    /*
    Older Monte-carlo implementation of option price estimator. Please note that the below
    implementation was both slow and wrong, as it didn't take into account the spot price.

    --------------------------------------------------------------------------------------

    public double getEstimate(double maturityPeriods, double strikePrice, OptionType optionType) {
        Function<Double, Double> payoffFunction = OptionPriceEstimator.getPayoffFunction(strikePrice, optionType);

        LogNormalDistribution finalPriceDistribution = stockPrice.getDistribution(maturityPeriods);

        double[] prices = finalPriceDistribution.getSamples(SAMPLES).data();

        double totalPayoff = 0.0;

        for (int pos = 0; pos < SAMPLES; pos++) {
            totalPayoff += payoffFunction.apply(prices[pos]);
        }

        double expectedValue = totalPayoff / SAMPLES;

        return expectedValue * discountFactor(maturityPeriods);
    }

    */

    private static Function<Double, Double> getPayoffFunction(double strikePrice, OptionType optionType) {
        if (optionType == OptionType.CALL) {
            return (S) -> Math.max(S - strikePrice, 0);
        } else if (optionType == OptionType.PUT) {
            return (S) -> Math.max(strikePrice - S, 0);
        } else {
            throw new RuntimeException("Unsupported Option Type.");
        }
    }

    private double discountFactor(double maturityPeriods) {
        return Math.exp(-riskFree * maturityPeriods);
    }

    public enum OptionType {
        CALL, PUT
    }


    // Shamelessly copied from https://stackoverflow.com/a/8377936/2882634
    // Computes the cumulative normal distribution for the standard normal variable.

    public static double CNDF(double x) {
        int neg = (x < 0d) ? 1 : 0;
        if ( neg == 1)
            x *= -1d;

        double k = (1d / ( 1d + 0.2316419 * x));
        double y = (((( 1.330274429 * k - 1.821255978) * k + 1.781477937) *
                k - 0.356563782) * k + 0.319381530) * k;
        y = 1.0 - 0.398942280401 * Math.exp(-0.5 * x * x) * y;

        return (1d - neg) * y + neg * (1d - y);
    }

}
