package gov.zndev.reviewlogclient;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;

public class StageReadyEvent extends ApplicationEvent {

    public final Stage main_stage;

    public StageReadyEvent(Stage stage) {
        super(stage);
        this.main_stage = stage;
    }


}
