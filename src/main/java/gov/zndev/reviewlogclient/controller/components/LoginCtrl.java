package gov.zndev.reviewlogclient.controller.components;

import gov.zndev.reviewlogclient.controller.MainController;
import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.models.User;
import gov.zndev.reviewlogclient.repositories.users.UsersRepo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FxmlView("login_layout.fxml")
public class LoginCtrl {


    private UsersRepo usersRepo;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @Autowired
    private FxWeaver fxWeaver;

    private Stage stage;


    @FXML
    public void initialize() {
        usersRepo= new UsersRepo();

        username.setOnAction(e -> {
            if (!username.getText().equals("")) {
                password.requestFocus();
            }
        });

        password.setOnAction(e -> {
            tryLogin();
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnHiding(windowEvent -> {

        });
    }

    private void tryLogin() {
        if (!username.getText().equals("") && !password.getText().equals("")) {
            usersRepo.checkUser(username.getText(), password.getText(), (success, message, object) -> {
                if (success) {
                    List<User> userList = (List<User>) object;
                    if (userList.size() > 0) {
                        Platform.runLater(() -> openMainApp(userList.get(0)));
                    }
                } else {
                    Platform.runLater(() -> {
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", message, null);
                        password.clear();
                    });

                }
            });
        } else {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please input username and password.", null);
        }
    }


    private void openMainApp(User user) {
        this.stage.hide();

        FxControllerAndView<MainController, AnchorPane> main = fxWeaver.load(MainController.class);

        Scene scene = new Scene(main.getView().get());

        this.stage.setScene(scene);
        this.stage.setResizable(true);
        this.stage.setMaximized(true);
        this.stage.setTitle("CCTV Review Logger");

        main.getController().setStage(this.stage);
        main.getController().setUser(user);
        this.stage.show();
    }

    @FXML
    void onLogin(ActionEvent event) {
        System.out.println("Login Attempt");
        tryLogin();
    }

    @FXML
    void onSettings(ActionEvent event) {
        FxControllerAndView<ConfigSettingsCtrl, AnchorPane> configSettings = fxWeaver.load(ConfigSettingsCtrl.class);

        Stage stage = Helper.CreateStage("Configuration Settings");
        Scene scene = new Scene(configSettings.getView().get());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(this.stage);

        stage.show();
    }
}
