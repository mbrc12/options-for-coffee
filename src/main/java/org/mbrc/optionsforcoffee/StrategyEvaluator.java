package org.mbrc.optionsforcoffee;

import javax.script.ScriptException;
import java.util.Arrays;

public class StrategyEvaluator {

    final static int DIVISIONS = 20;

    private String code;
    private ChartMaintainer chartMaintainer;
    private LoggingManager loggingManager;
    private GeometricBrownianMotion stockGBM;
    private double riskFreeRate;

    public StrategyEvaluator(String code,
                             ChartMaintainer chartMaintainer,
                             LoggingManager loggingManager,
                             GeometricBrownianMotion stockGBM,
                             double riskFreeRate) {
        this.code = code;
        this.chartMaintainer = chartMaintainer;
        this.loggingManager = loggingManager;
        this.stockGBM = stockGBM;
        this.riskFreeRate = riskFreeRate;
    }

    public void evaluate(int days, int iterCount) throws NoSuchMethodException, ScriptException {

        MathArray[] samples = stockGBM.generateSamplePaths(iterCount, days);

        OptionPriceEstimator optionPriceEstimator = new OptionPriceEstimator(stockGBM, riskFreeRate);

        SinglePathEvaluator singlePathEvaluator =
                new SinglePathEvaluator(code, loggingManager, optionPriceEstimator, stockGBM, riskFreeRate);

        double[] averagePosition = new double[days];
        double[] averageStock = new double[days];
        double[] minPosition = new double[days];
        double[] maxPosition = new double[days];
        double[] finalPositions = new double[iterCount];

        Arrays.fill(averagePosition, 0);


        for (int index = 0; index < iterCount; index++) {

            double[] stockData = samples[index].data();
            double[] portfolio = singlePathEvaluator.evaluate(index, stockData);

            for (int day = 0; day < days; day++) {
                averagePosition[day] += portfolio[day];
                averageStock[day] += stockData[day];
                minPosition[day] = Math.min(minPosition[day], portfolio[day]);
                maxPosition[day] = Math.max(maxPosition[day], portfolio[day]);
            }

            loggingManager.log("== Iteration " + index + " over.");
            finalPositions[index] = portfolio[days - 1];
        }

        for (int day = 0; day < days; day++) {
            averagePosition[day] /= iterCount;
            averageStock[day] /= iterCount;
        }

        chartMaintainer.clearSeries();
        chartMaintainer.requestSeriesPlot("Average Position", averagePosition);
        chartMaintainer.requestSeriesPlot("Average Stock", averageStock);
        chartMaintainer.requestSeriesPlot("Max Position", maxPosition);
        chartMaintainer.requestSeriesPlot("Min Position", minPosition);


        plotFinalDistribution(finalPositions);

        loggingManager.log("== Finished evaluation.");
    }

    public void plotFinalDistribution(double[] array) {

        double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;

        for (int pos = 0; pos < array.length; pos++) {
            max = Math.max(max, array[pos]);
            min = Math.min(min, array[pos]);
        }

        double[] edges = new double[DIVISIONS];
        double[] counts = new double[DIVISIONS];

        Arrays.fill(counts, 0);

        double step = (max - min) / DIVISIONS;

        edges[0] = min + step / 2;

        for (int pos = 1; pos < edges.length; pos++) {
            edges[pos] = edges[pos - 1] + step;
        }

        for (int pos = 0; pos < array.length; pos++) {
            double val = array[pos];
            int position = (int)Math.floor((val - min) / step);
            if (position >= DIVISIONS) position = DIVISIONS - 1;
            counts[position]++;
        }

        chartMaintainer.clearReturns();
        chartMaintainer.requestReturnsPlot("Distribution of Final Returns", edges, counts);
    }
}
