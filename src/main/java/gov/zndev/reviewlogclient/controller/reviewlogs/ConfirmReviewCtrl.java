package gov.zndev.reviewlogclient.controller.reviewlogs;

import gov.zndev.reviewlogclient.controller.MainController;
import gov.zndev.reviewlogclient.controller.components.PrintPreviewCtrl;
import gov.zndev.reviewlogclient.controller.components.PrintablePageCtrl;
import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import gov.zndev.reviewlogclient.models.Incident;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.ReviewLog;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.repositories.incidents.IncidentRepository;
import gov.zndev.reviewlogclient.repositories.reviewlogs.ReviewLogsRepository;
import gov.zndev.reviewlogclient.repositories.system.SystemDataRepository;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@FxmlView("confirm_review_layout.fxml")
public class ConfirmReviewCtrl {

    private static final Logger log = LoggerFactory.getLogger(ConfirmReviewCtrl.class);


    @FXML
    private TextField name;

    @FXML
    private TextField office;

    @FXML
    private TextField inclusive_dates;

    @FXML
    private TextField review_date;

    @FXML
    private ScrollPane scroll_pane;

    private ReviewLog reviewLog;
    private Personnel personnel;

    private List<Incident> incidents;

    @Autowired
    private FxWeaver fxWeaver;

    private ReviewLogsRepository reviewLogsRepo;

    private SystemDataRepository systemDataRepo;

    private IncidentRepository incidentRepo;

    private Stage stage;

    private MainController mainController;


    @FXML
    public void initialize() {
        reviewLogsRepo=new ReviewLogsRepository();
        systemDataRepo=new SystemDataRepository();
        incidentRepo=new IncidentRepository();

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setReviewInfo(ReviewLog reviewLog, Personnel personnel, List<Incident> incidents) {
        this.reviewLog = reviewLog;
        this.personnel = personnel;
        this.incidents = incidents;
        fixReviewLogData();
        populateFields();
    }

    private void fixReviewLogData() {

        // For review Id
        log.info("Fetching Last Review ID Data . . . .");
        systemDataRepo.getConfig(ResourceHelper.CONFIG_KEY_LASTREVIEWID, (success, message, object) -> {
            if (success) {

                Map<String, String> map = (Map<String, String>) object;
                if (map.get(ResourceHelper.CONFIG_KEY_LASTREVIEWID) != null) {
                    reviewLog.setId(Integer.parseInt(map.get(ResourceHelper.CONFIG_KEY_LASTREVIEWID))+1);
                    DecimalFormat df = new DecimalFormat("000000");
                    reviewLog.setReviewId(df.format(reviewLog.getId()));
                }

            } else {
                log.info("False: Error Msg " + message);
            }
        });

        // For Date Reviewed
        DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa");
        reviewLog.setFormattedReviewDate(dateFormat.format(new Date()));
    }


    private void populateFields() {
        this.name.setText(personnel.getFullName());
        this.office.setText(personnel.getOffice());
        this.inclusive_dates.setText(reviewLog.getInclusiveDates());
        this.review_date.setText(reviewLog.getFormattedReviewDate());
        setupPrintPreview();

    }

    private AnchorPane printablePage;

    private void setupPrintPreview() {
        FxControllerAndView<PrintPreviewCtrl, AnchorPane> printPreview = fxWeaver.load(PrintPreviewCtrl.class);
        FxControllerAndView<PrintablePageCtrl, AnchorPane> printablePage = fxWeaver.load(PrintablePageCtrl.class);
        printablePage.getController().setData(reviewLog, personnel, incidents);
        printPreview.getController().setPrintablePane(printablePage);
        scroll_pane.setContent(printPreview.getView().get());
        this.printablePage = printablePage.getView().get();
    }


    private void saveAndPrint(boolean printThePage) {
        reviewLogsRepo.createReviewLog(reviewLog, (success, message, object) -> {
            if (success) {
                log.info("Review Log Saved");
                List<ReviewLog> list = (List<ReviewLog>) object;
                if (list.size() > 0) {
                    ReviewLog savedReviewLog = list.get(0);

                    log.info("Saving Incidents . . .");
                    for (Incident incident : incidents) {
                        incident.setReviewLogId(savedReviewLog.getId());
                        incidentRepo.createIncident(incident, new RepoInterface() {
                            @Override
                            public void activityDone(Boolean success, String message, Object object) {
                                if (success) {
                                    log.info("Incident Saved");
                                }
                            }
                        });
                    }

                    // Print now
                    if (printThePage) {
                        new Thread(() -> {
                            printPage(printablePage);
                        }).start();
                    }
                    Platform.runLater(() -> {
                        if (printThePage) {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Message", "Saved Successfully", "Now printing, please wait.");
                        } else {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Message", "Saved Successfully", null);
                        }

                        stage.close();
                        mainController.clearReviewFields();

                    });
                }
            }
        });
    }

    private void saveOnly() {

    }


    @FXML
    void onSave(ActionEvent event) {
        saveAndPrint(false);
    }

    @FXML
    void onSaveAndPrint(ActionEvent event) {
        saveAndPrint(true);
    }


    private void printPage(Node node) {

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            PageLayout pageLayout = printerJob.getPrinter().createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, 0, 0, 0, 0);

            boolean success = printerJob.printPage(pageLayout, node);
            if (success) {
                printerJob.endJob();
            }
        }
    }
}
