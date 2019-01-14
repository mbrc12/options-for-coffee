package org.mbrc.optionsforcoffee;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

class ChartMaintainer {

    /* Charts are always marked from x = 0 .. length in this application */

    private LineChart<Double, Double> chartSeries;
    private AreaChart<Double, Double> chartReturns;

    public ChartMaintainer(LineChart<Double, Double> chartSeries,
                           AreaChart<Double, Double> chartReturns) {
        this.chartSeries = chartSeries;
        this.chartReturns = chartReturns;
        chartSeries.setCreateSymbols(false);
        chartReturns.setCreateSymbols(false);
    }

    private List<XYChart.Data<Double, Double>> convertToList(MathArray data) {
        double[] values = data.data();
        List<XYChart.Data<Double, Double>> list = new ArrayList<>();

        for (int j = 0; j < values.length; j++) {
            list.add(new XYChart.Data<Double, Double>((double)j, values[j]));
        }

        return list;
    }

    private List<XYChart.Data<Double, Double>> convertToList(double[] data) {
        List<XYChart.Data<Double, Double>> list = new ArrayList<>();

        for (int j = 0; j < data.length; j++) {
            list.add(new XYChart.Data<Double, Double>((double)j, data[j]));
        }

        return list;
    }

    private List<XYChart.Data<Double, Double>> convertToList(double[] dataX, double[] dataY) {
        List<XYChart.Data<Double, Double>> list = new ArrayList<>();

        for (int j = 0; j < dataX.length; j++) {
            list.add(new XYChart.Data<Double, Double>(dataX[j], dataY[j]));
        }

        return list;
    }

    public void requestSeriesPlot(String name, MathArray data) {

        XYChart.Series series = new XYChart.Series();

        List<XYChart.Data<Double, Double>> listData = convertToList(data);

        series.getData().setAll(listData);
        series.setName(name);

        chartSeries.getData().add(series);
    }

    public void requestSeriesPlot(String name, double[] data) {

        XYChart.Series series = new XYChart.Series();

        List<XYChart.Data<Double, Double>> listData = convertToList(data);

        series.getData().setAll(listData);
        series.setName(name);

        chartSeries.getData().add(series);
    }

    public void clearSeries() {
        chartSeries.getData().setAll();
    }

    public void requestReturnsPlot(String name, double[] dataX, double[] dataY) {

        XYChart.Series series = new XYChart.Series();

        List<XYChart.Data<Double, Double>> listData = convertToList(dataX, dataY);

        series.getData().setAll(listData);
        series.setName(name);

        chartReturns.getData().add(series);
    }

    public void clearReturns() {
        chartReturns.getData().setAll();
    }

}
