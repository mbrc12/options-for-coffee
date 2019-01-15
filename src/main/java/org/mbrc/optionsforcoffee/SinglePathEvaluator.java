package org.mbrc.optionsforcoffee;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class SinglePathEvaluator {

    private final double riskFreeRate;
    private String code;
    private LoggingManager loggingManager;
    private OptionPriceEstimator optionPriceEstimator;
    private GeometricBrownianMotion stockGBM;
    private ScriptEngine scriptEngine;

    public SinglePathEvaluator(String code,
                               LoggingManager loggingManager,
                               OptionPriceEstimator optionPriceEstimator,
                               GeometricBrownianMotion stockGBM,
                               double riskFreeRate) throws ScriptException {
        this.code = code;
        this.loggingManager = loggingManager;
        this.optionPriceEstimator = optionPriceEstimator;
        this.stockGBM = stockGBM;
        this.riskFreeRate = riskFreeRate;

        this.scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.eval(code);

    }


    public double[] evaluate(int iteration, double[] stockData) throws ScriptException, NoSuchMethodException {

        int days = stockData.length;

        InteractionManager interactionManager = new InteractionManager(loggingManager, iteration, optionPriceEstimator, stockGBM, riskFreeRate, stockData);

        Invocable invocable = (Invocable)scriptEngine;

        for (int day = 0; day < days; day++) {
            interactionManager.incrementDay(this);
            invocable.invokeFunction("strategy", interactionManager);
            interactionManager.finishDay(this);
        }

        return interactionManager.getPortfolioPath(this);
    }
}
