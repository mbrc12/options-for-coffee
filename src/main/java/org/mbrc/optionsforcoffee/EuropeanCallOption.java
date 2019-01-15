package org.mbrc.optionsforcoffee;

public class EuropeanCallOption implements Asset {

    private OptionPriceEstimator optionPriceEstimator;
    private int timeToMaturity;
    private double[] stockPath;
    private int currentDay;
    private double strike;

    public EuropeanCallOption(GeometricBrownianMotion stockGBM, double riskFree, double[] stockPath, int maturity, double strike, int currentDay) {

        this.optionPriceEstimator = new OptionPriceEstimator(stockGBM, riskFree);
        this.timeToMaturity = maturity;
        this.strike = strike;
        this.stockPath = stockPath;
        this.currentDay = currentDay;
    }

    @Override
    public double getCurrentValue() {

        double spotPrice = stockPath[currentDay];

        if (timeToMaturity <= 0) {
            return Math.max(spotPrice - strike, 0);
        }

        return optionPriceEstimator.getEstimate(spotPrice, timeToMaturity, strike, OptionPriceEstimator.OptionType.CALL);
    }

    @Override
    public void evolve(Object caller) {
        verifyCaller(caller);
        currentDay++;
        timeToMaturity--;
    }

    @Override
    public String getAssetType() {
        return "European Call";
    }

    public void verifyCaller(Object caller) {
        if (caller instanceof InteractionManager) return;
        throw new RuntimeException("Unauthorized method call.");
    }
}
