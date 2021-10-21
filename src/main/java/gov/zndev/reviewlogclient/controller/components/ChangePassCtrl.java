package gov.zndev.reviewlogclient.controller.components;

import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.models.User;
import gov.zndev.reviewlogclient.repositories.users.UsersRepo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("change_pass_layout.fxml")
public class ChangePassCtrl {

    @FXML
    private PasswordField re_enter_pass;

    @FXML
    private PasswordField new_password;

    @FXML
    private PasswordField current_password;

    private UsersRepo usersRepo;
    private User user;

    @FXML
    public void initialize() {
        usersRepo = new UsersRepo();
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void updatePassword() {
        if (validateFields()) {
            user.setPassword(new_password.getText());

            usersRepo.updateUser(user.getId(), user, (success, message, object) -> {
                if (success) {
                    Platform.runLater(() -> {
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Message", "Password Updated Successfully", null);
                        clearFields();
                    });
                } else {
                    // Error occurred

                    Platform.runLater(() -> {
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Error Occurred on Server", "Error:\n" + message);
                    });
                }
            });
        }
    }

    private void clearFields() {
        current_password.clear();
        new_password.clear();
        re_enter_pass.clear();
    }

    private boolean validateFields() {

        // Checks fields if not empty
        if (!current_password.getText().equals("") || !new_password.getText().equals("") || !re_enter_pass.getText().equals("")) {

            if (current_password.getText().equals(user.getPassword())) {
                if (new_password.getText().equals(re_enter_pass.getText())) {

                    return true;
                } else {
                    AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "New Password did not match!", null);
                    return false;
                }
            } else {
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Wrong Password!", "User's current password did not match.");
                return false;
            }

        } else {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Please fill all required Fields.", null);
            return false;
        }
    }

    @FXML
    void onUpdatePassword(ActionEvent event) {
        updatePassword();
    }
}
