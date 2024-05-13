
 import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HomeController {

    @FXML
    private StackPane addCashPane;

    @FXML
    private Pane balancePane;

    @FXML
    private Pane billsPane;

    @FXML
    private Pane foodPane;

    @FXML
    private Pane othersPane;

    @FXML
    private Pane shoppingPane;

    @FXML
    private StackPane tableViewPane;

    @FXML
    private Label totalBalanceLabel;

    @FXML
    private Label totalShoppingLabel;

    @FXML
    private Label totalFoodLabel;

    @FXML
    private Label totalBillsLabel;

    @FXML
    private Label totalOthersLabel;

    
    public void initialize() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddExpense.fxml"));
            Parent createTaskRoot = loader.load();
            addCashPane.getChildren().clear();
            addCashPane.getChildren().add(createTaskRoot);
        } catch (IOException e) {
            e.printStackTrace();
        } 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableView.fxml"));
            Parent createTaskRoot = loader.load();
            tableViewPane.getChildren().clear();
            tableViewPane.getChildren().add(createTaskRoot);
        } catch (IOException e) {
            e.printStackTrace();
        } 
        updateCategoryLabels();
        updateTotalBalanceLabel(); 

    }

    private void updateCategoryLabels() {
        // Get the total amounts for each category
        double totalShopping = getTotalFromExpenseFile("Shopping.txt");
        double totalFood = getTotalFromExpenseFile("Food&Drinks.txt");
        double totalBills = getTotalFromExpenseFile("Bills&Utilities.txt");
        double totalOthers = getTotalFromExpenseFile("OtherExpenses.txt");
    
        // Update labels with calculated totals
        totalShoppingLabel.setText(String.format("%.2f", totalShopping));
        totalFoodLabel.setText(String.format("%.2f", totalFood));
        totalBillsLabel.setText(String.format("%.2f", totalBills));
        totalOthersLabel.setText(String.format("%.2f", totalOthers));
    }
    
    private void updateTotalBalanceLabel() {
        // Get the total revenue from the "Income.txt" file
        double totalRevenue = getTotalFromExpenseFile("Income.txt");

        // Calculate the total spending
        double totalSpending = 0;
        totalSpending += getTotalFromExpenseFile("Shopping.txt");
        totalSpending += getTotalFromExpenseFile("Food&Drinks.txt");
        totalSpending += getTotalFromExpenseFile("Bills&Utilities.txt");
        totalSpending += getTotalFromExpenseFile("OtherExpenses.txt");

        // Calculate the total balance
        double totalBalance = totalRevenue - totalSpending;

        // Update the total balance label
        totalBalanceLabel.setText(String.format("%.2f", totalBalance));
    }
        
    

    private double getTotalFromExpenseFile(String fileName) {
        double total = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                double amount = Double.parseDouble(parts[1]);
                total += amount;
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return total;
    }

}

