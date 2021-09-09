package gov.zndev.reviewlogclient.controller.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("review_incident_item_layout.fxml")
public class ReviewIncidentItemCtrl {


    @FXML
    private Label date_time;

    @FXML
    private Button btnView;

    @FXML
    private Button btnRemove;


    public Label getDate_time() {
        return date_time;
    }

    public void setDate_time(Label date_time) {
        this.date_time = date_time;
    }

    public Button getBtnRemove() {
        return btnRemove;
    }
}
