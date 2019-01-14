package org.mbrc.optionsforcoffee;


import javax.swing.text.html.Option;
import java.util.function.Function;

public class OptionPriceEstimator {

    /* This library only works for European Options */

    final int SAMPLES = 20;

    private GeometricBrownianMotion stockPrice;
    private double riskFree;

    OptionPriceEstimator(GeometricBrownianMotion stockPrice, double riskFree) {
        this.stockPrice = stockPrice;
        this.riskFree = riskFree;
    }

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
        CALL, PUT;
    }
}
