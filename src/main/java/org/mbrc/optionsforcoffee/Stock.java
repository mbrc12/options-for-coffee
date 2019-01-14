package org.mbrc.optionsforcoffee;

public class Stock implements Asset {

    private double amount;
    private double[] stockPrice;
    private int currentDay;

    Stock(double amount, double[] stockPrice, int currentDay) {
        this.amount = amount;
        this.stockPrice = stockPrice;
        this.currentDay = currentDay;
    }

    @Override
    public double getCurrentValue() {
        return amount * stockPrice[currentDay];
    }

    @Override
    public void evolve(Object caller) {
        verifyCaller(caller);
        currentDay++;
    }

    @Override
    public String getAssetType() {
        return "Stock";
    }

    public void verifyCaller(Object caller) {
        if (caller instanceof InteractionManager) return;
        throw new RuntimeException("Unauthorized method call.");
    }
}
