import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class EditExpenseController {
    private tableController tableController;

    @FXML
    private CheckBox billsBox;

    @FXML
    private HBox categoryBox;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private TextField editSumField;

    @FXML
    private DatePicker editdatePicker;

    @FXML
    private TextField editpurposeField;

    @FXML
    private CheckBox expenseCheckBox;

    @FXML
    private CheckBox foodBox;

    @FXML
    private CheckBox incomeCheckBox;

    @FXML
    private CheckBox othersBox;

    @FXML
    private CheckBox shoppingBox;

    private Expense editedExpense;

    public void setEditedExpense(Expense expense) {
        this.editedExpense = expense;
    }

    public void setTableController(tableController controller) {
        this.tableController = controller;
    }    

        @FXML
    void deleteTransaction(ActionEvent event) { 
    // Remove the transaction from the file
    String category = editedExpense.getcategory();
    String fileName = getFileNameForCategory(category);
    if (fileName != null) {
        try {
            deleteExpenseFromFile(fileName, editedExpense);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Remove the transaction from the table
    tableController.removeExpense(editedExpense);

    // Close the edit window
    Stage stage = (Stage) deleteButton.getScene().getWindow();
    stage.close();
}

    @FXML
    void editTransaction(ActionEvent event) {
        // Retrieve the edited values
        String newPurpose = editpurposeField.getText();
        double newAmount = Double.parseDouble(editSumField.getText());
        LocalDate newDate = editdatePicker.getValue();
        String newCategory = ""; // Determine new category based on checkboxes
    
        // Logic to determine newCategory based on checkboxes
        if (foodBox.isSelected()) {
            newCategory = "Food";
        } else if (shoppingBox.isSelected()) {
            newCategory = "Shopping";
        } else if (billsBox.isSelected()) {
            newCategory = "Bills";
        } else if (othersBox.isSelected()) {
            newCategory = "Other";
        } else if (incomeCheckBox.isSelected()) {
            newCategory = "Revenue";
        }
    
        // Update the existing Expense
        editedExpense.setpurpose(newPurpose);
        editedExpense.setamount(newAmount);
        editedExpense.setdate(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        editedExpense.setcategory(newCategory);
    
        // Save the updated Expense to the file it belongs to
        saveExpenseToFile(newCategory, editedExpense);
        tableController.refreshTable();
    
        // Close the edit window
        Stage stage = (Stage) editButton.getScene().getWindow();
        stage.close();
    }
    
    private void saveExpenseToFile(String category, Expense expense) {
        // Get the file name based on the category
        String fileName = getFileNameForCategory(category);
        if (fileName != null) {
            try {
                // Delete the old expense from the file
                deleteExpenseFromFile(fileName, expense);

                // Add the updated expense to the file
                expense.writeToFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
    }

    private void deleteExpenseFromFile(String fileName, Expense expense) throws IOException {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;

        // Read each line from the file and add it to the list, excluding the line with the old expense
        // Edit based on transaction ID
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                int transactionID;
                try {
                    transactionID = Integer.parseInt(parts[4].trim().replace(";", ""));
                } catch (NumberFormatException e) {
                    continue; // Skip this line and proceed to the next
                }
                if (transactionID != expense.gettransactionID()) {
                    lines.add(line);
                }
            }
        }
    }
    // Write the updated list of lines back to the file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
        for (String newLine : lines) {
            writer.write(newLine);
            writer.newLine();
        }
    }
}

    // Method to get the file name based on the category
    private String getFileNameForCategory(String category) {
        switch (category) {
            case "Food":
                return "Food&Drinks.txt";
            case "Shopping":
                return "Shopping.txt";
            case "Bills":
                return "Bills&Utilities.txt";
            case "Other":
                return "OtherExpenses.txt";
            case "Revenue":
                return "Income.txt";
            default:
                return null;
        }
    }

    public void fillFields(Expense expense) {
        editpurposeField.setText(expense.getpurpose());
        editSumField.setText(String.valueOf(expense.getamount()));
        editdatePicker.setValue(expense.getdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        // Set checkboxes based on category
        String category = expense.getcategory();
        switch (category) {
            case "Food":
                foodBox.setSelected(true);
                expenseCheckBox.setSelected(true);
                incomeCheckBox.setDisable(true);
                break;
            case "Shopping":
                shoppingBox.setSelected(true);
                expenseCheckBox.setSelected(true);
                incomeCheckBox.setDisable(true);
                break;
            case "Bills":
                billsBox.setSelected(true);
                expenseCheckBox.setSelected(true);
                incomeCheckBox.setDisable(true);
                break;
            case "Other":
                othersBox.setSelected(true);
                expenseCheckBox.setSelected(true);
                incomeCheckBox.setDisable(true);
                break;
            case "Revenue":
                incomeCheckBox.setSelected(true);
                expenseCheckBox.setDisable(true);
                foodBox.setDisable(true);
                shoppingBox.setDisable(true);
                billsBox.setDisable(true);
                othersBox.setDisable(true);
                break;
            default:
                break;
        }
    }
}