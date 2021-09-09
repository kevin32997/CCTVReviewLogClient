package gov.zndev.reviewlogclient.controller.components;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import gov.zndev.reviewlogclient.models.Incident;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.time.ZoneId;


@FxmlView("edit_date_time_layout.fxml")
@Component
public class EditDateTimeCtrl {

    private Object ctrl;
    private Incident incident;
    private Stage stage;
    private boolean noTime;

    @FXML
    public void initialize() {

    }

    public void setIncident(Incident incident, boolean noTime) {
        this.incident = incident;
        this.noTime = noTime;
        setFields();
    }

    private void setFields() {
        this.date.setValue(incident.getIncidentDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());

        if (!noTime) {
            this.time.setValue(incident.getIncidentDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime());
        } else {
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void setCtrl(Object obj) {
        ctrl = obj;
    }

    private void updateIncident(){
        if(this.ctrl instanceof ViewIncidentCtrl){
            ((ViewIncidentCtrl)ctrl).updateIncidentDateTime(time.getValue(),date.getValue());
        }
        stage.close();
    }

    @FXML
    private JFXTimePicker time;

    @FXML
    private JFXDatePicker date;


    @FXML
    void onUpdateBtnClicked(ActionEvent event) {
        updateIncident();
    }


}
