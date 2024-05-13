# Finance 'lilBro
### 1. Purpose
The project is an expense tracker app that allows the user to add their expenses and revenues. It provides a friendly and simple platform for the user to read and analyze their expenditures. I named this app "Finanace 'lilBro" after the nickname I've given to my friend, who is obsessed with stocks and investing. 

### 2. Design
![Home Image](/Resources/FinalProjectHome.PNG)
When the program is run, the GUI window opens and the home screen has various components. The top coloured panes show the four different types of categories that exist and the total amount of expenditures the user has made in each category. There is also a green pane for balance. It shows the amount that is left after all the expenditures are cut from the total revenue.

At the bottom right of the window, there is an "Add Expense" pane in which the user can add the expenses and categorize them to a specific document. Each of the four categories have a different document in which they get stored in.
![Add Expense Pane](/Resources/AddExpense.PNG)

Beside the Add pane is the table view of all the transactions that the user has made. It shows the purpose, amount, date, category, and a unique 6-digit transaction ID of each transaction. 
![Table View](/Resources/TableView.PNG)
The program also allows the user to open up the transaction details and make edits to them by double-clicking on the transaction.
![Edit Window](/Resources/EditWindow.PNG)

Above the table, there are also various filters that the user can apply to organize the transactions.
![Filters](/Resources/Filter.PNG)

In the far left column, below the title, there are three buttons: Dashboard, Add Cash, and Quit. The Dashboard, opens the home scene, which is described above. 
The Add Cash button opens an Add Cash scene, in which the user adds his revenues. The revenue category has a specific txt file in which they are stores, like the expenses. 
![Add Cash](/Resources/AddCash.PNG)
The Quit button allows the user to quit the program, of course!

### 3. Functionality

##### 1. Saving an Expense
![Add Expense Pane](/Resources/AddExpense.PNG)
 ```
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
 ```
When the user enters all the valid info and clicks submit, the expense gets saved to a txt file depending on the category selected. For example, if the user selects the expense to be in the "Food" category, the expense will get saved in the "Food&Drinks.txt" file. Then, it will get displayed in the table when the page is refreshed. The code above shows how the txt file is determined and how the parameters are validated. After that, the expenses are written to the folder like:
```
public void writeToFile(String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        BufferedWriter bw = new BufferedWriter(fw);
        // Format the date as "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);
        
        bw.write(purpose + ", " + amount + ", " + category + ", " + formattedDate + "," + transactionID + ";\n"); 
        bw.close();
    }
```
##### 2. Adding Cash
The add cash works similar to the Add Expense function. But here, the user saves their revenue. When the user clicks on the submit button in this scene, the transaction gets saved to "Income.txt", if all the parameters entered are valid. When the user goes back to the dashboard/home page, they can see their income added in the table.

##### 3. Navigating the Table View
 ```
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
 ```
 This snippet of code shows how the table displays the transaction info. Another feature included in this app is the filter feature:
 ```
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
 ```
 Through this, the user can apply sort out their transactions based on date range, category, or the type: Income/Expense.
 
 As mentioned before, double-clicking on any transaction opens an edit window, in which you can make changes.
  ```
  private void openEditWindow(Expense) {
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
  ``` 

##### 4. Editing an Expense
Each transaction has a unique 6 digit transaction ID which differentiates it from other transactions, that could possible have the same purpose, amount, and/or date. The transaction ID makes it easier for the user to edit and delete any transaction stored. By double-clicking on any desired transaction, a new scene will open up with the details of the transaction, and will provide the user with two options: Submit, to confirm any edits made, or Delete, if the user wants to delete the transaction.
 ```
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

 ```
 When the edit window is opened, the fields obtain the information of the transaction (Excluding the unique transaction ID, as this will not be changed when edits are made). The user can change any parameter and click on the submit button to save the changes made, which is done using various other methods in the "EditController" class.

The other fuctionality in the edit window is the delete. 
(Refer to the code to see the entire deleteExpenseFromFile method)
 What this method does is that it finds the transaction with its unique ID and erases it from the tableView and the file it is in. After that, it updates the table to show the transaction without the deleted one.
 ##### 5. Quit
 ![Add Expense Pane](/Resources/Quit.PNG)
 Clicking on the quit button opens up a dialog which confirms if the user wants to quit or not:
 ```
  @FXML
    void quit(ActionEvent event) {
        // Opens a confirmation alert box...
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
         alert.setTitle("Confirmation Dialog");
         alert.setHeaderText("Quit");
         alert.setContentText("Are you sure you want to quit?");
         ButtonType yesButton = new ButtonType("Yes");
         ButtonType noButton = new ButtonType("No");
         alert.getButtonTypes().setAll(yesButton, noButton);
         alert.showAndWait().ifPresent(response -> {
             if (response == yesButton) {
                 System.out.println("Exited the program successfully!");
                 // If user clicks on yes button, the program will close.
                 System.exit(0);
             }
             // If user clicks on no button then the program will pick up where it left off.
         });
    }
 ```
