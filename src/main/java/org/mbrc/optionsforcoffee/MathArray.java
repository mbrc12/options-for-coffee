package org.mbrc.optionsforcoffee;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

/*
    Please note that this class only has functionality that
    is required by the code, and is not a general numPy-like
    array.
 */

public class MathArray {

    public double[] data;

    MathArray(int length) {
        data = new double[length];
        Arrays.fill(data, 0.0);
    }

    public void apply(Function<Double, Double> function, int l, int r) {
        for (int j = l; j < r; j++) {
            data[j] = function.apply(data[j]);
        }
    }

    public void apply(Function<Double, Double> function) {
        apply(function, 0, data.length);
    }

    public void muli(double x) {
        for (int j = 0; j < data.length; j++) {
            data[j] *= x;
        }
    }

    public void addi(double x) {
        for (int j = 0; j < data.length; j++) {
            data[j] += x;
        }
    }

    public void addi(int l, int r, double x) {
        for (int j = l; j < r; j++) {
            data[j] += x;
        }
    }

    public void logi()  {
        for (int j = 0; j < data.length; j++) {
            data[j] = Math.log(data[j]);
        }
    }

    public MathArray diff() {
        MathArray T = new MathArray(data.length - 1);
        for (int j = 1; j < data.length; j++) {
            T.data[j - 1] = data[j] - data[j - 1];
        }

        return T;
    }

    public void expi() {
        for (int j = 0; j < data.length; j++) {
            data[j] = Math.exp(data[j]);
        }
    }

    public void randn(long seed) {
        Random rng = new Random(seed);
        for (int j = 0; j < data.length; j++) {
            data[j] = rng.nextGaussian();
        }
    }

    public void randn() {
        randn(System.currentTimeMillis());
    }

    public void randn(int l, int r, long seed) {
        Random rng = new Random(seed);
        for (int j = l; j < r; j++) {
            data[j] = rng.nextGaussian();
        }
    }

    public void randn(int l, int r) {
        randn(l, r, System.currentTimeMillis());
    }

    public void cumsum() {
        for (int j = 1; j < data.length; j++) {
            data[j] += data[j - 1];
        }
    }

    public double[] data() {
        return data;
    }

    public int length() {
        return data.length;
    }

    public double get(int pos) {
        return data[pos];
    }

    public MathArray copy() {
        MathArray T = new MathArray(data.length);
        for (int j = 0; j < data.length; j++) {
            T.data[j] = data[j];
        }
        return T;
    }

    public double mean() {
        double sum = 0;
        for (int j = 0; j < data.length; j++) {
            sum += data[j];
        }
        return sum / data.length;
    }

    public double var() {

        if (data.length == 1) {
            throw new RuntimeException("Variance cannot be calculated for array of size 1");
        }

        double diffs = 0, mean = mean();

        for (int j = 0; j < data.length; j++) {
            diffs += (data[j] - mean) * (data[j] - mean);
        }

        return diffs / (data.length - 1);
    }

    public double std() {
        return Math.sqrt(var());
    }
}
