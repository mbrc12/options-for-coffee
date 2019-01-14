package org.mbrc.optionsforcoffee;

import java.util.function.Function;

public class Integrator {

    /* This is an adaptive Simpson quadrature. However, we employ
        a minimum depth to ensure that very sharp functions are also
        correctly integrated.
     */

    final static double epsilon = 1e-5;
    final static double infinity = 1e6;

    private final static int depth = 10;

    public static double integrate(Function<Double, Double> function, double l, double r) {
        double current = simpleSimpson(function, l, r);
        return adaptedSimpson(function, l, r, current, Integrator.depth);
    }

    private static double adaptedSimpson(Function<Double, Double> function, double l, double r, double current, int depth) {
        double m = (l + r) / 2;
        double leftPart = simpleSimpson(function, l, m);
        double rightPart = simpleSimpson(function, m, r);

        if (Math.abs(leftPart + rightPart - current) < Integrator.epsilon && depth < 0)
            return (leftPart + rightPart + current) / 2;

        return adaptedSimpson(function, l, m, leftPart, depth - 1) +
                adaptedSimpson(function, m, r, rightPart, depth - 1);
    }

    private static double simpleSimpson(Function<Double, Double> function, double l, double r) {
        double m = (l + r) / 2;
        double fa = function.apply(l);
        double fb = function.apply(r);
        double fm = function.apply(m);

        return ((r - l) / 6) * (fa + fb + fm * 4);
    }
}
