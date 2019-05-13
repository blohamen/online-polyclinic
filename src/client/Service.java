package client;

import javafx.scene.control.TextField;

public class Service {
    static public boolean isValid(String value) {
        return !value.trim().equals("");
    }

    static public boolean isInputValid(TextField... inputs) {
        int countValidInput = 0;
        for (TextField input : inputs) {
            if (!Service.isValid(input.getText())) {
                input.setStyle("-fx-border-color: red");
            } else {
                input.setStyle("");
                countValidInput += 1;
            }
        }
        return countValidInput == inputs.length;
    }
    static public final String getFormatString(String formatString, String separator , String ... texts) {
        String newString = formatString;
        for(String text: texts) {
            newString = newString.replaceFirst(separator, text);
        }
        return newString;
    }
}
