import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class AddExpenseController {

    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private TextField SumField;

    @FXML
    private CheckBox billsBox;

    @FXML
    private HBox categoryBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private CheckBox foodBox;

    @FXML
    private CheckBox othersBox;

    @FXML
    private TextField purposeField;

    @FXML
    private CheckBox shoppingBox;

    @FXML
    private Button submitButton;
    
    private Random random = new Random();

    @FXML
    public void initialize() {
        // Attach event handlers to checkboxes
        foodBox.setOnAction(event -> handleCheckboxSelection(foodBox));
        shoppingBox.setOnAction(event -> handleCheckboxSelection(shoppingBox));
        billsBox.setOnAction(event -> handleCheckboxSelection(billsBox));
        othersBox.setOnAction(event -> handleCheckboxSelection(othersBox));
    }

    @FXML
    void addExpense() {
        String purpose = purposeField.getText();
        double amount;
        Date date;
        try {
            amount = Double.parseDouble(SumField.getText());
            date = java.sql.Date.valueOf(datePicker.getValue());
        } catch (NumberFormatException | NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid amount and select a date.");
            return;
        }

        if (purpose.isEmpty() || !purpose.matches("[a-zA-Z0-9\\s]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Purpose", "Purpose must contain only alphanumeric characters.");
            return;
        }

        // Check if at least one checkbox is selected
        if (!foodBox.isSelected() && !shoppingBox.isSelected() && !billsBox.isSelected() && !othersBox.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "No Category Selected", "Please select at least one category.");
            return;
        }

        // Generate a random 6-digit ID
        int id = generateRandomID();

        // Method to get the respective file for each category...
        if (foodBox.isSelected()) {
            saveExpenseToFile("Food&Drinks.txt", purpose, amount, date, "Food", id);
        }
        if (shoppingBox.isSelected()) {
            saveExpenseToFile("Shopping.txt", purpose, amount, date, "Shopping", id);
        }
        if (billsBox.isSelected()) {
            saveExpenseToFile("Bills&Utilities.txt", purpose, amount, date, "Bills", id);
        }
        if (othersBox.isSelected()) {
            saveExpenseToFile("OtherExpenses.txt", purpose, amount, date, "Other", id);
        }

        // Clear all fields
        purposeField.clear();
        SumField.clear();
        datePicker.setValue(null);
        foodBox.setSelected(false);
        shoppingBox.setSelected(false);
        billsBox.setSelected(false);
        othersBox.setSelected(false);
    }

    private void saveExpenseToFile(String fileName, String purpose, double amount, Date date, String category, int id) {
        try {
            new Expense(purpose, amount, date, category, id).writeToFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCheckboxSelection(CheckBox selectedCheckbox) {
        // Iterate through checkboxes and unselect all except the selected one
        CheckBox[] checkboxes = {foodBox, shoppingBox, billsBox, othersBox};
        for (CheckBox checkbox : checkboxes) {
            if (!checkbox.equals(selectedCheckbox)) {
                checkbox.setSelected(false);
            }
        }
    }

    // Method to show alerts...
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to generate a random 6-digit ID
    private int generateRandomID() {
        return 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
    }
}
