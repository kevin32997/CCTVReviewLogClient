package gov.zndev.reviewlogclient.controller.components.printer;

import gov.zndev.reviewlogclient.helpers.Helper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@FxmlView("print_preview_layout.fxml")
@Component
public class PrintPreviewCtrl {
    @Autowired
    private FxWeaver fxWeaver;

    @FXML
    private AnchorPane main_pane;

    private FxControllerAndView<PrintablePageCtrl, AnchorPane> printable_pane;

    @FXML
    private Slider slider;

    @FXML
    private Button btnPrint;

    @FXML
    private Button btnPrintSettings;

    private PrinterJob printerJob;

    private Stage stage;


    @FXML
    private Label printerLabel;


    @FXML
    public void initialize() {

        slider.valueProperty().addListener((observableValue, number, t1) -> {
            printable_pane.getController().setWebZoom(t1.doubleValue() * 0.02);
        });

    }

    private void createPrintJobInstance() {
        printerJob = PrinterJob.createPrinterJob();
        printerLabel.setText(printerJob.getPrinter().getName());
    }


    private void print() {
        if (printerJob != null) {
            PageLayout pageLayout = printerJob.getPrinter().createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, 0, 0, 0, 0);
            System.out.println("Printing the document");
            boolean success = printerJob.printPage(pageLayout, printable_pane.getView().get());
            if (success) {
                System.out.println("Successfully printed");
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
        if (show) {
            btnPrint.setVisible(true);
            btnPrint.toFront();

            btnPrintSettings.setVisible(true);
            btnPrintSettings.toFront();

            printerLabel.setVisible(true);
            printerLabel.toFront();

            createPrintJobInstance();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        setupStage();
    }

    private void showPrinterOptions() {
        FxControllerAndView<PrinterListCtrl, AnchorPane> selectPrinter = fxWeaver.load(PrinterListCtrl.class);

        Stage stage = Helper.CreateStage("Select Printer");
        stage.setScene(new Scene(selectPrinter.getView().get()));
        stage.setResizable(false);

        selectPrinter.getController().createDialog(stage, new PrinterListCtrl.PrinterListListener() {
            @Override
            public void onPrinterSelected(Printer printer) {
                setPrinter(printer);
            }
        });
        stage.show();
    }

    private void setPrinter(Printer printer) {
        new Thread(() -> {
            try {
                printerJob.setPrinter(printer);
                Platform.runLater(() -> {
                    printerLabel.setText(printerJob.getPrinter().getName());
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    private void setupStage() {
        this.stage.setResizable(true);
    }

    @FXML
    void onPrint(ActionEvent event) {
        print();
    }

    @FXML
    void onPrintSettings(ActionEvent event) {
        showPrinterOptions();
    }


}
