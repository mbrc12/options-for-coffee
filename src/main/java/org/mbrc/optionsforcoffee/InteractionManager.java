package org.mbrc.optionsforcoffee;

import java.util.ArrayList;
import java.util.List;

public class InteractionManager {

    public final Direction LONG = Direction.LONG;
    public final Direction SHORT = Direction.SHORT;

    private final int days;
    private OptionPriceEstimator optionPriceEstimator;
    private GeometricBrownianMotion stockGBM;
    private double riskFreeRate;
    private double[] stockData;
    private double[] portfolio;

    private int currentDay;

    private Cash cash;          /* Cash is kept in the bank, either lend or borrow */

    private List<SignedAsset> assets;

    int iterationNumber;

    public InteractionManager(int iterationNumber,
                              OptionPriceEstimator optionPriceEstimator,
                              GeometricBrownianMotion stockGBM,
                              double riskFreeRate,
                              double[] stockData) {

        this.iterationNumber = iterationNumber;
        this.optionPriceEstimator = optionPriceEstimator;
        this.stockGBM = stockGBM;
        this.riskFreeRate = riskFreeRate;
        this.days = stockData.length;
        this.stockData = stockData;
        this.portfolio = new double[days];
        this.assets = new ArrayList<>();
        this.currentDay = -1;
        this.cash = new Cash(0, riskFreeRate);
    }

    /* Allow only the SinglePathEvaluator to use these methods */
    public void verifyCaller(Object caller) {
        if (caller instanceof SinglePathEvaluator) {
            return;
        } else {
            throw new RuntimeException("Attempted to call restricted method!");
        }
    }

    public double[] getPortfolioPath(Object caller) {
        verifyCaller(caller);
        return portfolio;
    }

    public void incrementDay(Object caller) {
        verifyCaller(caller);
        currentDay++;

        cash.evolve(this);

        for (Asset asset : assets) {
            asset.evolve(this);
        }
    }

    public void finishDay(Object caller) {
        verifyCaller(caller);

        double currentPortfolio = cash.getCurrentValue();

        for (Asset asset : assets) {
            currentPortfolio += asset.getCurrentValue();
        }

        portfolio[currentDay] = currentPortfolio;
    }

    /* Interaction methods for Strategy:

        Throughout, direction refers to short or long.
     */

    public void log(String info) {
        System.out.println("[" + iterationNumber + ", " + currentDay + "]: " + info);
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public double getCurrentCash() {
        return cash.getCurrentValue();
    }

    public List<SignedAsset> getAssets() {
        return this.assets;
    }

    private void addSignedAsset(SignedAsset asset) {
        cash.updateAmount(-asset.getCurrentValue()); // remove current value
        assets.add(asset);
    }

    public double getStockPrice() {
        return stockData[currentDay];
    }

    public double getStockPriceAt(int day) {
        return stockData[day];
    }

    public double getRiskFreeRate() {
        return riskFreeRate;
    }

    public double getCallPrice(double strike, int maturity) {
        return new EuropeanCallOption(stockGBM, riskFreeRate, stockData, maturity, strike, currentDay).getCurrentValue();
    }

    public double getPutPrice(double strike, int maturity) {
        return new EuropeanPutOption(stockGBM, riskFreeRate, stockData, maturity, strike, currentDay).getCurrentValue();
    }

    public void callOption(Direction direction, double strike, int maturity) {
        EuropeanCallOption callOption = new EuropeanCallOption(stockGBM, riskFreeRate, stockData, maturity, strike, currentDay);
        addSignedAsset(new SignedAsset(callOption, direction));
    }

    public void putOption(Direction direction, double strike, int maturity) {
        EuropeanPutOption putOption = new EuropeanPutOption(stockGBM, riskFreeRate, stockData, maturity, strike, currentDay);
        addSignedAsset(new SignedAsset(putOption, direction));
    }

    public void stock(int amount) {
        Stock stock = new Stock(amount, stockData, currentDay);
        addSignedAsset(new SignedAsset(stock, Direction.LONG)); // Long/shortness of stocks are already in amount
    }

    public enum Direction {
        LONG, SHORT;
    }
}
