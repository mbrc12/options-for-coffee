package org.mbrc.optionsforcoffee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Options For Coffee");
//        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        ChartMaintainer chartMaintainer = new ChartMaintainer(
                (LineChart<Double, Double>) scene.lookup("#chartSeries"),
                (AreaChart<Double, Double>) scene.lookup("#chartReturns"));

        LoggingManager loggingManager = new LoggingManager();

        GUIController controller = loader.getController();
        controller.init(chartMaintainer, loggingManager, primaryStage);

    }
}
