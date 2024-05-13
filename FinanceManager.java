import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FinanceManager extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FinanceManager.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Finance Manager");
        primaryStage.setScene(new Scene(root, 1200, 715));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
