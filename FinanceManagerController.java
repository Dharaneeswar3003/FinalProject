import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;

public class FinanceManagerController {

    @FXML
    private Button addCashButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button quitButton;

    @FXML
    private StackPane mainStackPane;

    private Alert alert;

    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent createTaskRoot = loader.load();
            mainStackPane.getChildren().clear();
            mainStackPane.getChildren().add(createTaskRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addCash(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddCash.fxml"));
            Parent createTaskRoot = loader.load();
            mainStackPane.getChildren().clear();
            mainStackPane.getChildren().add(createTaskRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent createTaskRoot = loader.load();
            mainStackPane.getChildren().clear();
            mainStackPane.getChildren().add(createTaskRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void quit(ActionEvent event) {
        // Opens a confirmation alert box...
        alert = new Alert(Alert.AlertType.CONFIRMATION); // Initialize the alert
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
}

