package gov.zndev.reviewlogclient.controller.components;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import gov.zndev.reviewlogclient.controller.MainController;
import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.models.Incident;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Component
@FxmlView("add_incident_layout.fxml")
public class AddIncidentDescriptionCtrl {
    private static final Logger log = LoggerFactory.getLogger(AddIncidentDescriptionCtrl.class);

    @FXML
    private JFXTimePicker time;

    @FXML
    private JFXDatePicker date;

    @FXML
    private Button btnAdd;

    @FXML
    private WebView webview;

    @Autowired
    private FxWeaver fxWeaver;

    private WebEngine webEngine;

    private MainController mainCtrl;
    private Stage stage;

    public static final int MODE_ADD = 0;
    public static final int MODE_EDIT = 1;


    private int list_index = -1;
    private int mode = 0;
    private String description = "";


    private void initEditor() {

        /*
        Resource resource = new ClassPathResource("html/ckeditor/samples/index.html");

        try {
            webEngine.load(resource.getURL().toExternalForm());
            log.info("Web File is loaded!");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Web File error",e);
        }
         */

        File file = new File("ckeditor/samples/index.html");
        log.info("File Exist: "+file.exists());
        log.info("File Location: "+ file.toURI());

        webEngine = webview.getEngine();
        webEngine.load(file.toURI().toString());
        webEngine.getLoadWorker().stateProperty().addListener((observableValue, state, t1) -> {
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("invoke", this);
            webEngine.executeScript("setDescription(`" + description + "`)");
        });

        webEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
            @Override
            public void changed(ObservableValue<? extends Throwable> observableValue, Throwable throwable, Throwable t1) {
                log.warn("Received Exception: " + t1.getMessage());
            }
        });
    }

    public void setMainCtrl(MainController mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setData(Date date, boolean noTime, String description, int mode, int list_index) {
        if (date != null) {
            this.date.setValue(date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());

            if (!noTime) {
                this.time.setValue(date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime());
            }
        }

        this.mode = mode;
        this.list_index = list_index;

        if (mode == MODE_EDIT) {
            btnAdd.setText("Update");
        }

        if (!description.equals("")) {
            this.description = description;
        }

        initEditor();


    }

    private void addIncident() {

        if (validateFields()) {
            checkEditorData();

            /*
            Incident incident=new Incident();
            incident.setDay(date.getValue().toString());
            incident.setTime(time.getValue().toString());

            DateFormat dateFormat=new SimpleDateFormat("yyyy-mm-dd hh:mm");

            try {
                incident.setIncidentDate(dateFormat.parse(incident.getDay()+" "+incident.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONObject json= XML.toJSONObject(description.getHtmlText());

            JSONObject jsonObjectHtml=json.getJSONObject("html");
            JSONObject jsonObjectBody=jsonObjectHtml.getJSONObject("body");
            System.out.println(jsonObjectBody.toString(4));
            System.out.println("To XML: "+XML.toString(jsonObjectBody));

            incident.setDescription(XML.toString(jsonObjectBody));



            mainCtrl.addIncident(incident);
            stage.close();
             */


        } else {
            // Error Message;
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Please select DATE and TIME.", null);
        }

    }

    private void checkEditorData() {
        webEngine.executeScript("loadData()");

    }

    public void setIncidentDescription(String description) {
        if (!description.equals("")) {
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

            if (mode == MODE_ADD) {
                mainCtrl.addIncident(incident);
            }
            if (mode == MODE_EDIT) {
                mainCtrl.updateIncident(incident, list_index);
            }

            stage.close();
        } else {
            // Alert Code
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "No Description Added!", "Please add description to proceed.");
        }
    }


    private boolean validateFields() {
        if (date.getValue() == null) {
            return false;
        }
        return true;
    }

    public void reloadEditor(){
        initEditor();
    }


    @FXML
    void onAdd(ActionEvent event) {
        addIncident();
    }

}
