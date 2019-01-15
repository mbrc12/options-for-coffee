package org.mbrc.optionsforcoffee;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class LoggingManager {

    private static final int MAX_LOG_SIZE = 100000;
    private StringBuffer logBuilder;

    public void showConsole() {
        String logs = logBuilder.toString();

        if (logs.isEmpty()) {
            logs = "Nothing here!";
        } else if (logs.length() > LoggingManager.MAX_LOG_SIZE) {
            logs += "\n <First ~ " + LoggingManager.MAX_LOG_SIZE + " characters only>";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Console Logs");
        alert.setHeaderText("Console Logs");

        TextArea textArea = new TextArea(logs);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setExpandableContent(textArea);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }

    public void log(String log) {
        if (logBuilder.length() > LoggingManager.MAX_LOG_SIZE) return;
        logBuilder.append("\n" + log);
    }

    public void clear() {
        logBuilder.delete(0, logBuilder.length());
    }

    public LoggingManager() {
        this.logBuilder = new StringBuffer();
    }
}
