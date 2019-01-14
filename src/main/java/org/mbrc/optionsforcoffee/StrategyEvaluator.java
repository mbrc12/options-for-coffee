package org.mbrc.optionsforcoffee;

import javafx.scene.control.Label;
import javax.script.ScriptException;
import java.util.Arrays;

public class StrategyEvaluator {

    final static int DIVISIONS = 100;

    private String code;
    private ChartMaintainer chartMaintainer;
    private GeometricBrownianMotion stockGBM;
    private double riskFreeRate;

    public StrategyEvaluator(String code, ChartMaintainer chartMaintainer, GeometricBrownianMotion stockGBM, double riskFreeRate) {
        this.code = code;
        this.chartMaintainer = chartMaintainer;
        this.stockGBM = stockGBM;
        this.riskFreeRate = riskFreeRate;
    }

    public void evaluate(int days, int iterCount, Label status) throws NoSuchMethodException, ScriptException {

        MathArray[] samples = stockGBM.generateSamplePaths(iterCount, days);

        OptionPriceEstimator optionPriceEstimator = new OptionPriceEstimator(stockGBM, riskFreeRate);

        SinglePathEvaluator singlePathEvaluator =
                new SinglePathEvaluator(code, optionPriceEstimator, stockGBM, riskFreeRate);

        double[] averageOnDay = new double[days];
        double[] finalPositions = new double[iterCount];

        Arrays.fill(averageOnDay, 0);

        for (int index = 0; index < iterCount; index++) {

            double[] stockData = samples[index].data();
            double[] portfolio = singlePathEvaluator.evaluate(index, stockData);

            for (int day = 0; day < days; day++) {
                averageOnDay[day] += portfolio[day];
            }

            status.setText("Done with iteration: " + index);
            finalPositions[index] = portfolio[days - 1];
        }

        for (int day = 0; day < days; day++) {
            averageOnDay[day] /= iterCount;
        }

        System.out.println("Final positions: " + Arrays.toString(finalPositions));

        chartMaintainer.clearSeries();
        chartMaintainer.requestSeriesPlot("Average Position", averageOnDay);

        plotFinalDistribution(finalPositions);
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

        System.out.println(Arrays.toString(edges));
        System.out.println(Arrays.toString(counts));

        chartMaintainer.clearReturns();
        chartMaintainer.requestReturnsPlot("Distribution of Final Returns", edges, counts);
    }
}
