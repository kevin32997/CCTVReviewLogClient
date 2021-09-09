package gov.zndev.reviewlogclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

public class FXApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(ReviewlogclientApplication.class)
                .run(args);
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        applicationContext.publishEvent(new StageReadyEvent(primaryStage)); //(2)
    }

    /*
    @Override
    public void start(Stage stage) throws Exception {
        FxWeaver fxWeaver = getWeaver();
        Parent root = fxWeaver.loadView(MainController.class);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("wew");
        //MainController ctrl = fxWeaver.loadController(MainController.class);
          //  ctrl.setStage(stage);
        stage.show();
    }

    @Bean
    public static FxWeaver getWeaver(){
        return applicationContext.getBean(FxWeaver.class);
    }

     */



}
