package gov.zndev.reviewlogclient.controller.components;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.models.Incident;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import netscape.javascript.JSObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@FxmlView("incident_creator_layout.fxml")
public class IncidentCreatorCtrl {

    private WebEngine webEngine;
    private IncidentCreatorListener listener;
    private Stage stage;

    @FXML
    public void initialize() {
    }

    public void buildEditor(Stage stage, IncidentCreatorListener listener) {
        this.stage = stage;
        this.listener = listener;

        System.out.println("Building Editor, fetching file . .");
        File file = new File("ckeditor/samples/index.html");
        System.out.println("File Fetched, check if exist");
        if (file.exists()) {
            System.out.println("File exist, loading to web engine");
            this.webEngine = webview.getEngine();
            webEngine.load(file.toURI().toString());

            System.out.println("File Loaded, adding window to html");

            // Register this Controller class to html file
            webEngine.getLoadWorker().stateProperty().addListener((observableValue, state, t1) -> {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("invoke", this);
            });
            System.out.println("Build Done!");
        }
    }

    public void buildEditor(Stage stage, LocalDate localDate, LocalTime localTime, String description, IncidentCreatorListener listener) {
        this.stage = stage;
        this.listener = listener;
        if (localDate != null) {
            date.setValue(localDate);
        }
        if (localTime != null) {
            time.setValue(localTime);
        }
        System.out.println("Building Editor, fetching file . .");
        File file = new File("ckeditor/samples/index.html");
        System.out.println("File Fetched, check if exist");
        if (file.exists()) {
            System.out.println("File exist, loading to web engine");
            this.webEngine = webview.getEngine();
            webEngine.load(file.toURI().toString());

            System.out.println("File Loaded, adding window to html");

            // Register this Controller class to html file
            webEngine.getLoadWorker().stateProperty().addListener((observableValue, state, t1) -> {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("invoke", this);
                webEngine.executeScript("setDescription(`" + description + "`)");
            });
            System.out.println("Build Done!");
        }
    }


    private void createIncident() {
        webEngine.executeScript("loadData()");
    }

    public void setIncidentDescription(String description) {
        if (description.equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Description is Empty!", "Add description first to continue.");
        } else if (date.getValue() == null) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "No Date Selected!", "Select Date to proceed.");
        } else {
            Incident incident = new Incident();
            incident.setDay(date.getValue().toString());
            if (time.getValue() != null) {
                incident.setTime(time.getValue().toString());
                LocalDateTime dt = LocalDateTime.of(date.getValue(), time.getValue());
                incident.setIncidentDate(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()));
            } else {
                incident.setTime("");
                incident.setIncidentDate(Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            incident.setDescription(description);

            listener.onIncidentCreated(incident);
            stage.close();
        }
    }

    @FXML
    private JFXTimePicker time;

    @FXML
    private JFXDatePicker date;

    @FXML
    private Button btnAdd;

    @FXML
    private WebView webview;

    @FXML
    void onAdd(ActionEvent event) {
        createIncident();
    }

    public interface IncidentCreatorListener {
        void onIncidentCreated(Incident incident);
    }
}
