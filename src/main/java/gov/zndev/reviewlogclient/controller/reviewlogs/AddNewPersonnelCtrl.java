package gov.zndev.reviewlogclient.controller.reviewlogs;

import gov.zndev.reviewlogclient.controller.MainController;
import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
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
@FxmlView("add_new_personnel_layout.fxml")
public class AddNewPersonnelCtrl {

    @FXML
    private TextField firstname;

    @FXML
    private TextField surname;

    @FXML
    private TextField middlename;

    @FXML
    private TextField office;

    @FXML
    private TextField designation;

    ////////////////////


    private PersonnelRepo personnelRepo;

    private MainController mainCtrl;
    private Stage stage;


    @FXML
    public void initialize() {
        personnelRepo=new PersonnelRepo();
    }

    public void setCtrl(MainController ctrl) {
        this.mainCtrl = ctrl;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInitName(String name){
        this.firstname.setText(name);
    }

    private void saveNewPersonnel() {
        if (validateSavePersonnel()) {
            Personnel personnel = new Personnel();
            personnel.setFirstName(this.firstname.getText());
            personnel.setLastName(this.surname.getText());
            personnel.setMiddleName(this.middlename.getText());
            personnel.setOffice(this.office.getText());
            personnel.setPosition(this.designation.getText());
            personnel.setAddedBy(ResourceHelper.MAIN_USER.getId());

            personnelRepo.createdPersonnel(personnel, new RepoInterface() {
                @Override
                public void activityDone(Boolean success, String message, Object object) {
                    if (success) {
                        List<Personnel> list= (List<Personnel>) object;
                        Platform.runLater(() -> {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Information", "Added Successfully", null);
                            stage.close();
                            mainCtrl.setReviewPersonnelData(list.get(0));
                        });
                    }
                }
            });
        }
    }

    private boolean validateSavePersonnel() {
        if (firstname.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please enter First name!", null);
            firstname.requestFocus();
            return false;
        } else if (surname.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please enter Surname!", null);
            surname.requestFocus();
            return false;
        } else if (office.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please fill Office and Designation.", null);
            office.requestFocus();
        } else if (designation.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please fill Office and Designation.", null);
            return false;
        }

        return true;
    }



    @FXML
    void onSave(ActionEvent event) {
        saveNewPersonnel();
    }


}
