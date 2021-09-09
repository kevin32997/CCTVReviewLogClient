package gov.zndev.reviewlogclient.helpers;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class AlertDialogHelper {

    public static void ShowSimpleAlertDialog(Alert.AlertType type, String title, String header, String context) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        alert.showAndWait();
    }


    public static final Alert ShowConfirmationAlertDialog(String title, String header, String context) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        /*
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                // ... user chose OK
            } else {
                // ... user chose CANCEL or closed the dialog
            }
         */
        return alert;
    }

    public static JFXDialog CreateJFXDialog(AnchorPane main_pane, Node header, Node body){
        StackPane stackPane=new StackPane();
        main_pane.getChildren().add(stackPane);

        // Set Anchors
        main_pane.setTopAnchor(stackPane,0.0);
        main_pane.setBottomAnchor(stackPane,0.0);
        main_pane.setLeftAnchor(stackPane,0.0);
        main_pane.setRightAnchor(stackPane,0.0);

        JFXDialogLayout content=new JFXDialogLayout();
        content.setHeading(header);
        content.setBody(body);


        JFXDialog dialog=new JFXDialog(stackPane,content, JFXDialog.DialogTransition.CENTER);

        dialog.setOnDialogClosed(e->{
            main_pane.getChildren().remove(stackPane);
        });

        return dialog;
    }

}
