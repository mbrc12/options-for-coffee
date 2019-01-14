package org.mbrc.optionsforcoffee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
//        GeometricBrownianMotion gbm = new GeometricBrownianMotion();
//        INDArray samplePath = gbm.generateSamplePaths(1, 100).getRow(0l);
//        System.out.println(Arrays.toString(samplePath.data().asDouble()));
//
//
//        gbm = GeometricBrownianMotion.estimate(samplePath, 1);
//
//        System.out.println(gbm.getMu() + " " + gbm.getSigma());


//        Interpreter interpreter = new Interpreter();
//
//        try {
//            interpreter.set("foo", 9);
//            interpreter.eval("bar = foo * 2; bar = Math.log(bar)");
//            System.out.println(interpreter.get("bar"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        launch(args);

//        LogNormalDistribution logNormalDistribution = new LogNormalDistribution(0.0, 10);

//        System.out.println(logNormalDistribution.pdf(logNormalDistribution.mean()));


//        GeometricBrownianMotion geometricBrownianMotion = new GeometricBrownianMotion(0.006, 0.015, 152.29);
//        LogNormalDistribution logNormalDistribution = geometricBrownianMotion.getDistribution(6);

//        System.out.println(logNormalDistribution.mean() + ", " + logNormalDistribution.std() + ", " + logNormalDistribution.sigma);

//        System.out.println((logNormalDistribution.integrateOverDistribution((x) -> Math.max(x - 100.0, 0), Integrator.epsilon, Integrator.infinity))/Math.exp(0.0241 * 6 / 360));
    }


    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI.fxml"));
        Scene scene = new Scene((Parent)loader.load());

        primaryStage.setTitle("Options For Coffee");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        ChartMaintainer chartMaintainer = new ChartMaintainer(
                (LineChart<Double, Double>) scene.lookup("#chartSeries"),
                (AreaChart<Double, Double>) scene.lookup("#chartReturns"));

        GUIController controller = loader.<GUIController>getController();
        controller.init(chartMaintainer, primaryStage);

    }
}
