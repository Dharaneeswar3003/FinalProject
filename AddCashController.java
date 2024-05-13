import java.io.IOException;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class AddCashController {

    @FXML
    private TextField SourceSumField;

    @FXML
    private DatePicker sourceDatePicker;

    @FXML
    private TextField sourceField;

    @FXML
    private Button submitButton;

    private Random random = new Random();

    @FXML
void addCash(ActionEvent event) {
    String purpose = sourceField.getText();
    String amountText = SourceSumField.getText();
    java.util.Date utilDate = null;
    double amount = 0;

    // Validate purpose field
    if (purpose.isEmpty() || !purpose.matches("[a-zA-Z0-9\\s]+")) {
        showAlert(Alert.AlertType.ERROR, "Invalid Purpose", "Purpose must contain only alphanumeric characters.");
        return;
    }

    // Validate amount field
    try {
        amount = Double.parseDouble(amountText);
        if (amount <= 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Amount must be a positive number.");
            return;
        }
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid amount.");
        return;
    }

    // Validate date picker
    if (sourceDatePicker.getValue() == null) {
        showAlert(Alert.AlertType.ERROR, "Invalid Date", "Please select a date.");
        return;
    } else {
        // Convert the LocalDate to Date
        utilDate = java.sql.Date.valueOf(sourceDatePicker.getValue());
    }

    String category = "Revenue"; 
    int id = generateRandomID();
    
    // Create the new object
    Expense expense = new Expense(purpose, amount, utilDate, category, id);
    try {
        // Write the income to "Income.txt" file
        expense.writeToFile("Income.txt");
    } catch (IOException e) {
        e.printStackTrace();
    }
    // Clear fields after submission
    sourceField.clear();
    SourceSumField.clear();
    sourceDatePicker.setValue(null);
}


    // Method to generate a random 6-digit ID
    private int generateRandomID() {
        return 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
    }

    //Method to show alerts...
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
