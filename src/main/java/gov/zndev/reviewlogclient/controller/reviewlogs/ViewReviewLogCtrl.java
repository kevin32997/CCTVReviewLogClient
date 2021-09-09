package gov.zndev.reviewlogclient.controller.reviewlogs;

import gov.zndev.reviewlogclient.controller.components.IncidentCreatorCtrl;
import gov.zndev.reviewlogclient.controller.components.PrintPreviewCtrl;
import gov.zndev.reviewlogclient.controller.components.PrintablePageCtrl;
import gov.zndev.reviewlogclient.controller.components.ViewIncidentCtrl;
import gov.zndev.reviewlogclient.controller.personnel.ViewPersonnelCtrl;
import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.models.Incident;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.ReviewLog;
import gov.zndev.reviewlogclient.models.User;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.repositories.incidents.IncidentRepository;
import gov.zndev.reviewlogclient.repositories.users.UsersRepo;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@FxmlView("view_review_log_layout.fxml")
public class ViewReviewLogCtrl {

    @FXML
    private TextField personnel_name;

    @FXML
    private TextField personnel_office;

    @FXML
    private TextField review_date;

    @FXML
    private TextField cyclops;

    @FXML
    private TableView<Incident> incident_table;
    @FXML
    private TableColumn<Incident, String> cl_date;

    @FXML
    private TableColumn<Incident, String> cl_time;

    @FXML
    private TextField review_id;

    @FXML
    private TextField inclusive_dates;

    ////////////////////////////////////////////////////////*****************************************************

    private IncidentRepository incidentRepo;
    private UsersRepo usersRepo;

    @Autowired
    private FxWeaver fxWeaver;

    private Stage stage;
    private ReviewLog reviewLog;
    private List<Incident> incidents;
    private boolean isEditable = true;


    @FXML
    public void initialize() {
        incidentRepo = new IncidentRepository();
        usersRepo = new UsersRepo();
        setupTable();
    }

    private void setupTable() {
        this.cl_date.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty(new SimpleDateFormat("MMMMM dd, yyyy").format(c.getValue().getIncidentDate()));
            }
            return new SimpleStringProperty("<no data>");
        });

        this.cl_time.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                if (c.getValue().getTime().equals("")) {
                    return new SimpleStringProperty("<no data>");
                }
                return new SimpleStringProperty(new SimpleDateFormat("hh:mm:ss aaa").format(c.getValue().getIncidentDate()));
            }
            return new SimpleStringProperty("<no data>");
        });

        ContextMenu cm = new ContextMenu();
        this.incident_table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                viewIncident(this.incident_table.getSelectionModel().getSelectedItem());
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                cm.getItems().clear();

                MenuItem actionView = new MenuItem("View");
                actionView.setOnAction(event -> {
                    viewIncident(incident_table.getSelectionModel().getSelectedItem());
                });

                cm.getItems().add(actionView);
                if(isEditable) {
                    MenuItem actionRemove = new MenuItem("Remove");
                    actionRemove.setOnAction(event -> {
                        System.out.println("Removing Incident from review . . .");
                        removeIncident(incident_table.getSelectionModel().getSelectedItem());
                    });
                    cm.getItems().add(actionRemove);
                }
                cm.show(incident_table, e.getScreenX(), e.getScreenY());
            }

            if (e.getButton() == MouseButton.PRIMARY) {
                cm.hide();
            }
        });

        incident_table.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                cm.hide();
            }
        });
    }

    private void viewIncident(Incident incident) {

        FxControllerAndView<ViewIncidentCtrl, AnchorPane> viewIncident = fxWeaver.load(ViewIncidentCtrl.class);

        viewIncident.getController().setData(incident, reviewLog, reviewLog.getPersonnel());
        viewIncident.getController().setListener(() -> {
            // was updated
            Platform.runLater(() -> {
                        refreshFields();
                    }
            );
        });

        // Init Stage
        Stage stage = Helper.CreateStage("View Incident");
        Scene scene = new Scene(viewIncident.getView().get(), 739, 551);
        stage.setScene(scene);

        viewIncident.getController().setStage(stage);

        stage.initOwner(this.stage);
        stage.show();
    }

    private void removeIncident(Incident incident) {
        incidentRepo.deleteIncident(incident.getId(), (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {

                    AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Message", "Incident Deleted!", null);
                    Helper.UpdateReviewLogInclusiveDates(reviewLog, (success1, message1, obl) -> {
                        if (success) {
                            System.out.println("ViewReviewLogCtrl: removeIncident(): Review log was Updated too. . .");
                            Platform.runLater(() -> {
                                refreshFields();
                            });
                        }
                    });
                });
            } else {
                System.out.println("ViewReviewLogCtrl: removeIncident(): " + message);
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setReviewLog(ReviewLog reviewLog) {
        this.reviewLog = reviewLog;
        populateFields();
        fetchIncidents();
        setupReviewLogPrintableData();
        checkReviewEditable();
    }

    private void setupReviewLogPrintableData() {
        reviewLog.setReviewId(new DecimalFormat("000000").format(reviewLog.getId()));
        reviewLog.setFormattedReviewDate(new SimpleDateFormat("MMMMM dd,yyyy hh:mm:ss aaaa").format(reviewLog.getReviewDate()));
        usersRepo.getUserById(reviewLog.getReviewerId(), new RepoInterface() {
            @Override
            public void activityDone(Boolean success, String message, Object object) {
                if (success) {
                    List<User> list = (List<User>) object;
                    if (!list.isEmpty()) {
                        reviewLog.setReviewerCodeName(list.get(0).getUsername());
                    }
                }
            }
        });

    }

    private void populateFields() {
        DecimalFormat df = new DecimalFormat("000000");
        DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa");
        review_id.setText(df.format(reviewLog.getId()));
        inclusive_dates.setText(reviewLog.getInclusiveDates());
        review_date.setText(dateFormat.format(reviewLog.getReviewDate()));
        cyclops.setText(reviewLog.getReviewerName());
        personnel_name.setText(reviewLog.getPersonnel().getFullName());
        personnel_office.setText(reviewLog.getPersonnel().getOffice());
    }

    private void fetchIncidents() {
        incidentRepo.getIncidentByReviewLog(reviewLog.getId(), (success, message, object) -> {
            if (success) {
                incidents = (List<Incident>) object;
                if (incidents.size() > 0) {
                    incident_table.getItems().clear();
                    incident_table.getItems().addAll(incidents);
                }
            }
        });
    }

    private void openPrintPreview() {
        // Add missing data
        FxControllerAndView<PrintPreviewCtrl, AnchorPane> printPreview = fxWeaver.load(PrintPreviewCtrl.class);
        FxControllerAndView<PrintablePageCtrl, AnchorPane> printablePage = fxWeaver.load(PrintablePageCtrl.class);
        printablePage.getController().setData(reviewLog, reviewLog.getPersonnel(), incidents);
        printPreview.getController().setPrintablePane(printablePage);
        printPreview.getController().showPrintBtn(true);

        // Init Stage
        Stage stage = Helper.CreateStage("Print Preview");
        Scene scene = new Scene(printPreview.getView().get(), 596, 815);
        stage.setScene(scene);

        printPreview.getController().setStage(stage);

        stage.initOwner(this.stage);
        stage.show();
    }

    private void openPersonnel(Personnel personnel) {
        FxControllerAndView<ViewPersonnelCtrl, AnchorPane> viewUser = fxWeaver.load(ViewPersonnelCtrl.class);
        viewUser.getController().setPersonnel(personnel);

        Stage stage = Helper.CreateStage("View Personnel - " + personnel.getFullName());
        Scene scene = new Scene(viewUser.getView().get());
        stage.initOwner(this.stage);

        stage.setScene(scene);
        stage.setResizable(false);

        viewUser.getController().setStage(stage);
        stage.show();
    }

    private void openIncidentEditor() {
        FxControllerAndView<IncidentCreatorCtrl, AnchorPane> incidentCreator = fxWeaver.load(IncidentCreatorCtrl.class);

        Stage stage = Helper.CreateStage("Create Incident");
        stage.setScene(new Scene(incidentCreator.getView().get()));

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(this.stage);

        incidentCreator.getController().buildEditor(stage, incident -> {
            System.out.println("Incident Added?");
            addIncidentToReview(incident);
        });

        stage.show();

    }

    private void addIncidentToReview(Incident incident) {
        incident.setReviewLogId(reviewLog.getId());
        incidentRepo.createIncident(incident, (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {
                    AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Message", "Incident Added Successfully", null);
                    Helper.UpdateReviewLogInclusiveDates(reviewLog, (success1, message1, obl) -> {
                        // Refresh Data
                        Platform.runLater(() -> {
                            refreshFields();
                        });
                    });
                });
            } else {
                System.out.println("Error while adding incident\nError: " + message);
            }
        });
    }


    private void checkReviewEditable() {
        Date reviewDate = reviewLog.getReviewDate();
        Date date = new Date();
        long time_difference = date.getTime() - reviewDate.getTime();
        // Calucalte time difference in days
        long days_difference = (time_difference / (1000 * 60 * 60 * 24)) % 365;
        // Calucalte time difference in years
        long years_difference = (time_difference / (1000l * 60 * 60 * 24 * 365));
        // Calucalte time difference in seconds
        long seconds_difference = (time_difference / 1000) % 60;
        // Calucalte time difference in minutes
        long minutes_difference = (time_difference / (1000 * 60)) % 60;

        // Calucalte time difference in hours
        long hours_difference = (time_difference / (1000 * 60 * 60)) % 24;
        // Show difference in years, in days, hours, minutes, and seconds
        System.out.println(
                hours_difference
                        + " hours, "
                        + minutes_difference
                        + " minutes, "
                        + seconds_difference
                        + " seconds, "
                        + years_difference
                        + " years, "
                        + days_difference
                        + " days"
        );

        if (days_difference >= 1f) {
            isEditable = false;
            disableEdit();
        }
    }

    private void disableEdit() {
        btnAddIncident.setVisible(false);
    }

    public void refreshFields() {
        populateFields();
        fetchIncidents();
        setupReviewLogPrintableData();
    }


    @FXML
    private Button btnAddIncident;

    @FXML
    void onAddIncidentBtnClicked(ActionEvent event) {
        openIncidentEditor();
    }

    @FXML
    void onPrintPreview(ActionEvent event) {
        openPrintPreview();
    }

    @FXML
    void onViewPersonnel(ActionEvent event) {
        openPersonnel(reviewLog.getPersonnel());
    }


}
