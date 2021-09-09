package gov.zndev.reviewlogclient.helpers;

import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class SearchResultListView extends ListView<String> {

    private Control control;
    private List<?> list;

    public SearchResultListView(Control control,double width, double offset) {
        super();
        System.out.println("width is "+width);
        this.control=control;
        this.list=new ArrayList<>();
        this.setPrefWidth(width);

        this.setLayoutX(control.getLayoutX());
        this.setLayoutY(control.getLayoutY()+offset);
        this.setVisible(false);
    }


    public void displayResult(List<String> list){
        Platform.runLater(()->{
            this.setDisable(false);
            this.getItems().clear();
            this.getItems().addAll(list);
            this.setPrefHeight(25*list.size());
        });
    }

    public void displayNoDataFound(String msg){
        Platform.runLater(()->{
            this.setDisable(true);
            this.getItems().clear();
            this.getItems().add(msg);
            this.setPrefHeight(25);
        });
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public void setPreferredWidth(double width){
        this.setPrefWidth(width);
    }
}
