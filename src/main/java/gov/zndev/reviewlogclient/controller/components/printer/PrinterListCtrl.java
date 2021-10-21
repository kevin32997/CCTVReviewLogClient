package gov.zndev.reviewlogclient.controller.components.printer;

import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("printer_list_dialog_layout.fxml")
public class PrinterListCtrl {

    @Autowired
    private FxWeaver fxWeaver;

    @FXML
    private ListView<AnchorPane> printer_list;

    private Stage stage;
    private PrinterListListener listener;

    @FXML
    public void initialize() {
        displayPrinters();
    }

    private void displayPrinters() {
        ObservableSet<Printer> printers = Printer.getAllPrinters();
        for (Printer printer : printers) {
            addPrinterToList(printer);
        }
    }

    private void addPrinterToList(Printer printer) {
        AnchorPane item = fxWeaver.loadView(PrinterListItemCtrl.class);
        ((Label) item.lookup("#printer_name")).setText(printer.getName());
        ((Button) item.lookup("#btn_select")).setOnAction(e -> {
            selectPrinter(printer);
        });

        printer_list.getItems().add(item);
    }

    public void createDialog(Stage stage, PrinterListListener listener) {
        this.stage = stage;
        this.listener = listener;
    }

    private void selectPrinter(Printer printer){
        listener.onPrinterSelected(printer);
        stage.close();
    }

    public interface PrinterListListener {
        void onPrinterSelected(Printer printer);
    }

}
