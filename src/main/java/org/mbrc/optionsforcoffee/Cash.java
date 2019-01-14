package org.mbrc.optionsforcoffee;

public class Cash implements Asset {

    private double current;
    private double riskFreeRate;

    public Cash(double initial, double riskFreeRate) {
        this.current = initial;
        this.riskFreeRate = riskFreeRate;
    }

    @Override
    public double getCurrentValue() {
        return this.current;
    }

    @Override
    public void evolve(Object caller) {
        verifyCaller(caller);
        current *= Math.exp(this.riskFreeRate);
    }

    public void updateAmount(double amount) {
        current += amount;
    }

    @Override
    public String getAssetType() {
        return "Cash";
    }

    public void verifyCaller(Object caller) {
        if (caller instanceof InteractionManager) return;
        throw new RuntimeException("Unauthorized method call.");
    }
}
