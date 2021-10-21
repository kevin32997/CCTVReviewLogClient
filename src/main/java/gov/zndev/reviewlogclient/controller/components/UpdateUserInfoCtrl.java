package gov.zndev.reviewlogclient.controller.components;

import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.models.User;

import gov.zndev.reviewlogclient.repositories.users.UsersRepo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("update_user_info_layout.fxml")
public class UpdateUserInfoCtrl {


    @FXML
    private TextField fullname;

    @FXML
    private TextField username;

    @FXML
    private ComboBox<String> user_role;

    private UsersRepo usersRepo;
    private User user;

    @FXML
    public void initialize() {
        usersRepo = new UsersRepo();
        setupFields();
    }

    private void setupFields() {
        user_role.getItems().addAll(User.ROLE_ENCODER, User.ROLE_ADMIN);
    }

    public void setUser(User user) {
        this.user = user;

        // Setup current user's info
        fullname.setText(user.getFullname());
        username.setText(user.getUsername());
        user_role.getSelectionModel().select(user.getUserRole());
    }


    private void updateUserInfo() {
        if (validateFields()) {
            user.setFullname(fullname.getText());
            user.setUsername(username.getText());

            usersRepo.updateUser(user.getId(), user, (success, message, object) -> {
                if (success) {
                    Platform.runLater(() -> {
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Message", "User Info Updated Successfully", null);
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Error Occurred on Server", "Error:\n" + message);
                    });
                }
            });
        }

    }

    private boolean validateFields() {
        if (username.getText().equals("") || fullname.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Please fill all required Fields.", null);
            return false;
        }
        return true;
    }


    @FXML
    void onUpdateInfo(ActionEvent event) {
        updateUserInfo();
    }


}
