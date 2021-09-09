package gov.zndev.reviewlogclient.controller.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import netscape.javascript.JSObject;
import org.springframework.stereotype.Component;

import java.io.File;


@Component
@FxmlView("description_editor_layout.fxml")
public class DescriptionEditorCtrl {


    private EditorListener delegate;
    private WebEngine webEngine;
    private Stage stage;

    @FXML
    public void initialize() {
    }

    public void buildEditor(String description) {
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

    // Calling this cause html file to run its script codes then calls setIncidentDescription() in this file
    private void checkEditorData() {
        webEngine.executeScript("loadData()");
    }

    public void setIncidentDescription(String description) {
        delegate.onSaveDescription(description);
        stage.close();
    }

    public void setEditorListener(EditorListener editorInterface) {
        this.delegate = editorInterface;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private WebView webview;

    @FXML
    void onSaveBtnClicked(ActionEvent event) {
        checkEditorData();
    }

    interface EditorListener {
        void onSaveDescription(String description);
    }


}
