package gov.zndev.reviewlogclient.controller.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;


@FxmlView("print_preview_layout.fxml")
@Component
public class PrintPreviewCtrl {
    @FXML
    private AnchorPane main_pane;

    private FxControllerAndView<PrintablePageCtrl, AnchorPane> printable_pane;

    @FXML
    private Slider slider;

    @FXML
    private Button btnPrint;

    private Stage stage;


    @FXML
    public void initialize() {

        slider.valueProperty().addListener((observableValue, number, t1) -> {
            printable_pane.getController().setWebZoom(t1.doubleValue() * 0.02);
        });
    }

    @FXML
    void onPrint(ActionEvent event) {
        print();
    }

    private void print() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            PageLayout pageLayout = printerJob.getPrinter().createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, 0, 0, 0, 0);
            boolean success = printerJob.printPage(pageLayout, printable_pane.getView().get());
            if (success) {
                printerJob.endJob();
            }
        }
    }

    public void setPrintablePane(FxControllerAndView<PrintablePageCtrl, AnchorPane> printablePane) {
        this.printable_pane = printablePane;

        main_pane.getChildren().add(printable_pane.getView().get());
        main_pane.setBottomAnchor(printable_pane.getView().get(), 0.0);
        main_pane.setLeftAnchor(printable_pane.getView().get(), 0.0);
        main_pane.setRightAnchor(printable_pane.getView().get(), 0.0);

        printable_pane.getController().setWebZoom(28 * 0.02);
    }

    public void showPrintBtn(boolean show) {
        btnPrint.setVisible(show);
        btnPrint.toFront();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        setupStage();
    }

    private void setupStage() {
        this.stage.setResizable(true);
    }


}
