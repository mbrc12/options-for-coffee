package org.mbrc.optionsforcoffee;

public class EuropeanPutOption implements Asset {

    private OptionPriceEstimator optionPriceEstimator;
    private int timeToMaturity;
    private double[] stockPath;
    private int currentDay;
    private double strike;

    public EuropeanPutOption(GeometricBrownianMotion stockGBM, double riskFree, double[] stockPath, int maturity, double strike, int currentDay) {

        this.optionPriceEstimator = new OptionPriceEstimator(stockGBM, riskFree);
        this.timeToMaturity = maturity;
        this.strike = strike;
        this.stockPath = stockPath;
        this.currentDay = currentDay;
    }

    @Override
    public double getCurrentValue() {
        if (timeToMaturity <= 0) {
            return Math.max(strike - stockPath[currentDay], 0);
        }

        return optionPriceEstimator.getEstimate(timeToMaturity, strike, OptionPriceEstimator.OptionType.PUT);
    }

    @Override
    public void evolve(Object caller) {
        verifyCaller(caller);
        currentDay++;
        timeToMaturity--;
    }

    @Override
    public String getAssetType() {
        return "European Put";
    }

    public void verifyCaller(Object caller) {
        if (caller instanceof InteractionManager) return;
        throw new RuntimeException("Unauthorized method call.");
    }
}