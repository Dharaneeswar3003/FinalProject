import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;

public class tableController {

    @FXML
    private DatePicker fromDateFilter;

    @FXML
    private DatePicker toDateFilter;

    @FXML
    private CheckBox incomeFilter;

    @FXML
    private CheckBox expenseFilter;

    @FXML
    private ComboBox<String> categoryFilter;

    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private TableColumn<Expense, String> purposeColumn;

    @FXML
    private TableColumn<Expense, String> categoryColumn;

    @FXML
    private TableColumn<Expense, Number> sumColumn;

    @FXML
    private TableColumn<Expense, Date> dateColumn;

    @FXML
    private TableColumn<Expense, Number> idColumn;

    public void initialize() {

        categoryFilter.getItems().addAll("Food", "Shopping", "Bills", "Other", "All");
        categoryFilter.setValue("All");

        dateColumn.setCellValueFactory(data -> {
            Date date = data.getValue().getdate();
            return new SimpleObjectProperty<>(date);
        });

        // Set cell value factories for each column
        purposeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getpurpose()));
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getcategory()));
        sumColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getamount()));
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().gettransactionID()));

        dateColumn.setCellFactory(column -> {
            TableCell<Expense, Date> cell = new TableCell<Expense, Date>() {
                private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(format.format(item));
                    }
                }
            };
            return cell;
        });
        sumColumn.setCellFactory(column -> new TableCell<Expense, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = item.toString();
                    TableRow<Expense> row = getTableRow();
                    if (row != null) {
                        Expense expense = row.getItem();
                        if (expense != null) {
                            if ((expense.getcategory().equals("Revenue"))) {
                                //Revenue - display in green with a "+" sign
                                text = "+" + text;
                                setTextFill(Color.GREEN);
                            } else {
                                text = "-" + text;
                                // Expense - display in red
                                setTextFill(Color.RED);
                            }
                        }
                    }
                    setText(text);
                }
            }
        });
        ObservableList<Expense> allExpenses = FXCollections.observableArrayList();
        loadExpensesFromFile("Food&Drinks.txt", allExpenses);
        loadExpensesFromFile("Income.txt", allExpenses);
        loadExpensesFromFile("Shopping.txt", allExpenses);
        loadExpensesFromFile("Bills&Utilities.txt", allExpenses);
        loadExpensesFromFile("OtherExpenses.txt", allExpenses);

        // Create a filtered list
        FilteredList<Expense> filteredExpenses = new FilteredList<>(allExpenses);
        expenseTable.setItems(filteredExpenses);

        // Organize the elements in the table
        allExpenses.sort(Comparator.comparing(Expense::getdate).reversed());

        // Update the filter predicate
        fromDateFilter.setOnAction(event -> applyFilters(filteredExpenses));
        toDateFilter.setOnAction(event -> applyFilters(filteredExpenses));
        incomeFilter.setOnAction(event -> applyFilters(filteredExpenses));
        expenseFilter.setOnAction(event -> applyFilters(filteredExpenses));
        categoryFilter.setOnAction(event -> applyFilters(filteredExpenses));

        expenseTable.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {
                    Expense selectedExpense = row.getItem();
                    openEditWindow(selectedExpense);
                }
            });
            return row;
        });
    }

    private void applyFilters(FilteredList<Expense> filteredExpenses) {
        filteredExpenses.setPredicate(expense -> {
            // Filter by date range
            Date fromDate = fromDateFilter.getValue() != null ? Date.from(fromDateFilter.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            Date toDate = toDateFilter.getValue() != null ? Date.from(toDateFilter.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            boolean dateInRange = (fromDate == null || expense.getdate().after(fromDate) || expense.getdate().equals(fromDate)) &&
                    (toDate == null || expense.getdate().before(toDate) || expense.getdate().equals(toDate));

            // Filter by income or expense
            boolean isIncome = incomeFilter.isSelected();
            boolean isExpense = expenseFilter.isSelected();
            boolean incomeCondition = isIncome && expense.getcategory().equals("Revenue");
            boolean expenseCondition = isExpense && !expense.getcategory().equals("Revenue");

            // Filter by category
            String selectedCategory = categoryFilter.getValue();
            boolean categoryMatch = selectedCategory == null || selectedCategory.equals("All") || expense.getcategory().equals(selectedCategory);

            // Apply filters
            return dateInRange && ((isIncome && incomeCondition) || (isExpense && expenseCondition) || (!isIncome && !isExpense)) && categoryMatch;
        });
    }

    private void loadExpensesFromFile(String fileName, ObservableList<Expense> expenses) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] transactions = line.split(";");
                for (String transaction : transactions) {
                    String[] parts = transaction.split(",");
                    if (parts.length == 5) { // Ensure that the line has the correct number of parts
                        String purpose = parts[0].trim();
                        double amount = Double.parseDouble(parts[1].trim());
                        String dateString = parts[3].trim();
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                        String category = parts[2].trim();
                        
                        // Parse the transaction ID
                        int transactionID = Integer.parseInt(parts[4].trim());
    
                        expenses.add(new Expense(purpose, amount, date, category, transactionID)); // Pass the transaction ID
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println("Error loading expenses from file: " + fileName);
        }
    }
    
    public void removeExpense(Expense expense) {
        ObservableList<Expense> newItems = FXCollections.observableArrayList(expenseTable.getItems()); // Create a new list
        newItems.remove(expense); // Remove the expense from the new list
        expenseTable.setItems(newItems); // Set the Table items to the new list
    }

    private void openEditWindow(Expense expense) {
        try {
            // Load the edit window FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditExpense.fxml"));
            Parent root = loader.load();
    
            // Get the controller of the edit window
            EditExpenseController editController = loader.getController();
            editController.setTableController(this);
            editController.setEditedExpense(expense);
            //Fill the edit window with selected item's details
            editController.fillFields(expense);
    
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open edit window.");
        }
    }

    public void refreshTable() {
        ObservableList<Expense> allExpenses = FXCollections.observableArrayList();
        loadExpensesFromFile("Food&Drinks.txt", allExpenses);
        loadExpensesFromFile("Income.txt", allExpenses);
        loadExpensesFromFile("Shopping.txt", allExpenses);
        loadExpensesFromFile("Bills&Utilities.txt", allExpenses);
        loadExpensesFromFile("OtherExpenses.txt", allExpenses);
    
        // Set the TableView items directly with the new ObservableList
        expenseTable.setItems(allExpenses);
        expenseTable.refresh(); // Refresh the Table
        allExpenses.sort(Comparator.comparing(Expense::getdate).reversed());
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
