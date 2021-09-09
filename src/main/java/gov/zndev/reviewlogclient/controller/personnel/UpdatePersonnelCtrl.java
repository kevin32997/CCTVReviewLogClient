package gov.zndev.reviewlogclient.controller.personnel;

import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.repositories.personnels.PersonnelRepo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@FxmlView("update_personnel_layout.fxml")
public class UpdatePersonnelCtrl {

    @FXML
    private TextField per_firsname;

    @FXML
    private TextField per_surname;

    @FXML
    private TextField per_middlename;

    @FXML
    private TextField per_office;

    @FXML
    private TextField per_designation;

    ///////////////////////////////////////////////////////////////

    private PersonnelRepo personnelRepo;
    private Personnel per;
    private Stage stage;

    private ViewPersonnelCtrl ctrl;

    @FXML
    public void initialize() {
        personnelRepo = new PersonnelRepo();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCtrl(ViewPersonnelCtrl ctrl) {
        this.ctrl = ctrl;
    }

    public void setPersonnel(Personnel personnel) {
        this.per = personnel;
        populateFields();
    }

    private void populateFields() {
        this.per_firsname.setText(per.getFirstName());
        this.per_surname.setText(per.getLastName());
        this.per_middlename.setText(per.getMiddleName());
        this.per_office.setText(per.getOffice());
        this.per_designation.setText(per.getPosition());
    }

    private void updatePersonnel() {
        if (validateSavePersonnel()) {
            this.per.setFirstName(per_firsname.getText());
            this.per.setLastName(per_surname.getText());
            this.per.setMiddleName(per_middlename.getText());
            this.per.setOffice(per_office.getText());
            this.per.setPosition(per_designation.getText());

            // Update to server
            personnelRepo.updatePersonnel(per.getId(), per, new RepoInterface() {
                @Override
                public void activityDone(Boolean success, String message, Object object) {
                    if (success) {
                        List<Personnel> list = (List<Personnel>) object;
                        Platform.runLater(() -> {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Info", "Update Successful", "Personnel was successfully updated.");
                            stage.close();
                            ctrl.setPersonnel(list.get(0));
                        });
                    }
                }
            });
        }
    }

    private boolean validateSavePersonnel() {
        if (per_firsname.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please enter First name!", null);
            per_firsname.requestFocus();
            return false;
        } else if (per_surname.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please enter Surname!", null);
            per_surname.requestFocus();
            return false;
        } else if (per_office.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please fill Office and Designation.", null);
            per_surname.requestFocus();
        } else if (per_designation.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please fill Office and Designation.", null);
            return false;
        }
        return true;
    }

    @FXML
    void onUpdate(ActionEvent event) {
        updatePersonnel();
    }
}
