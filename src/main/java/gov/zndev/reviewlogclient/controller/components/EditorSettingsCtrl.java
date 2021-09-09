package gov.zndev.reviewlogclient.controller.components;

import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@FxmlView("editor_settings_layout.fxml")
public class EditorSettingsCtrl {

    @FXML
    private TextField editor_url;

    private AddIncidentDescriptionCtrl parentCtrl;


    @FXML
    public void initialize() {
        this.editor_url.setText(ResourceHelper.EDITOR_URL);
    }

    @FXML
    void onLoad(ActionEvent event) {
        parentCtrl.reloadEditor();
    }

    public void setParentCtrl(AddIncidentDescriptionCtrl parentCtrl) {
        this.parentCtrl = parentCtrl;
    }

    @FXML
    void onSaveConfigurations(ActionEvent event) {
        if (!editor_url.getText().equals("")) {
            String url = editor_url.getText();
            ResourceHelper.EDITOR_URL = url;
            try {
                Helper.UpdateConfigurations();
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Info", "Configuration Saved", null);
            } catch (IOException e) {
                e.printStackTrace();
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Exception Occurred", e.getMessage());
            }
        } else {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Field cannot be empty", "Default: "+ResourceHelper.DEFAULT_EDITOR_URL);
            editor_url.setText(ResourceHelper.EDITOR_URL);
        }
    }
}
