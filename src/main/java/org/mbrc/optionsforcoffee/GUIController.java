package org.mbrc.optionsforcoffee;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.script.ScriptException;
import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GUIController {

    @FXML
    private TextField muValue;

    @FXML
    private TextField sigmaValue;

    @FXML
    private Slider startValue;

    @FXML
    private Slider stockDays;

    @FXML
    private TextField fileName;

    @FXML
    private Slider iterations;

    @FXML
    private Slider riskFree;

    @FXML
    private Label status;

    private ChartMaintainer chartMaintainer;
    private FileChooser fileChooser;
    private Stage stage;
    private File currentFile;

    public void showSamplePath(MouseEvent mouseEvent) {

        try {

            double mu = Double.parseDouble(muValue.getText());
            double sigma = Double.parseDouble(sigmaValue.getText());
            double start = startValue.getValue();
            int days = (int)stockDays.getValue();

            GeometricBrownianMotion geometricBrownianMotion = new GeometricBrownianMotion(mu, sigma, start);

            MathArray samplePath = geometricBrownianMotion.generateSamplePaths(1, days)[0];

            chartMaintainer.clearSeries();
            chartMaintainer.requestSeriesPlot("Sample Stock", samplePath);

        } catch (Exception e) {
            exceptionHandler(e);
        }
    }

    public void pickFile(MouseEvent mouseEvent) {
        File file = fileChooser.showOpenDialog(this.stage);
        String absPath = file.getAbsolutePath();
        fileName.setText(absPath);
        this.currentFile = file;
    }

    public void runStrategy(MouseEvent mouseEvent) {



        try {

            double mu = Double.parseDouble(muValue.getText());
            double sigma = Double.parseDouble(sigmaValue.getText());
            double start = startValue.getValue();
            int days = (int) stockDays.getValue();
            double riskFreeRate = riskFree.getValue() / 360.0;

            int iterCount = (int) iterations.getValue();

            GeometricBrownianMotion geometricBrownianMotion = new GeometricBrownianMotion(mu, sigma, start);

            String code = new String(Files.readAllBytes(this.currentFile.toPath()));

            StrategyEvaluator strategyEvaluator =
                    new StrategyEvaluator(code, chartMaintainer, geometricBrownianMotion, riskFreeRate);

            strategyEvaluator.evaluate(days, iterCount, status);

        } catch (Exception e) {
            exceptionHandler(e);
        }

    }

    public void exceptionHandler(Exception e) {

        if (e instanceof NumberFormatException) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter numbers in mu and sigma.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                return;
            }
        } else if (e instanceof NoSuchMethodException) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please define the strategy function in your script.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                return;
            }
        } else if (e instanceof ScriptException) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "--Error in script:-- \n" + e.getMessage());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                return;
            }
        } else {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Some error occurred.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                return;
            }
        }
    }

    public void init(ChartMaintainer chartMaintainer, Stage stage) {
        this.chartMaintainer = chartMaintainer;
        this.fileChooser = new FileChooser();
        this.stage = stage;
    }
}
