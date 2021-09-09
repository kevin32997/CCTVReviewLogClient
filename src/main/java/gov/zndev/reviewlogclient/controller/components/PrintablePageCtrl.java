package gov.zndev.reviewlogclient.controller.components;


import gov.zndev.reviewlogclient.models.Incident;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.ReviewLog;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.rgielen.fxweaver.core.FxmlView;
import netscape.javascript.JSObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.List;

import java.io.IOException;

@Component
@FxmlView("printable_page_layout.fxml")
public class PrintablePageCtrl {

    @FXML
    private WebView webView;

    private WebEngine webEngine;

    private ReviewLog reviewLog;
    private Personnel personnel;
    private List<Incident> incidents;
    private String incidentString;


    @FXML
    public void initialize() {

    }


    public WebView getWebView() {
        return webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public void setData(ReviewLog reviewLog, Personnel personnel, List<Incident> incidents) {
        this.reviewLog = reviewLog;
        this.personnel = personnel;
        this.incidents=incidents;
        formatIncidentDescription();
        loadData();

    }

    private void formatIncidentDescription(){

        StringBuilder sb=new StringBuilder();

        for(Incident incident:incidents){

            if(incident.getTime().equals("")){
                sb.append("<small><u>"+new SimpleDateFormat("MMMMM dd, yyyy").format(incident.getIncidentDate())+"</u></small>");
            }else{
                sb.append("<small><u>"+new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa").format(incident.getIncidentDate())+"</u></small>");
            }

            sb.append("\n");
            sb.append(incident.getDescription());
            sb.append("\n");
        }
        incidentString=sb.toString();
    }

    public void setWebZoom(Double value) {
        webView.setZoom(value);
    }

    private void loadData() {
        webEngine = webView.getEngine();
        Resource resource = new ClassPathResource("html/print_page.html");
        try {
            webEngine.load(resource.getURL().toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject jsObject = (JSObject) webEngine.executeScript("window");
                jsObject.call("setData", reviewLog, personnel, incidentString);
            }
        });


    }
}
