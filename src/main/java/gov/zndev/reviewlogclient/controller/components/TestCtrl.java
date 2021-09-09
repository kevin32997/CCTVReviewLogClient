package gov.zndev.reviewlogclient.controller.components;

import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("test_layout.fxml")
public class TestCtrl {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }



}
