package gov.zndev.reviewlogclient.controller.components;

import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.models.Incident;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.ReviewLog;
import gov.zndev.reviewlogclient.models.User;
import gov.zndev.reviewlogclient.repositories.incidents.IncidentRepository;
import gov.zndev.reviewlogclient.repositories.personnels.PersonnelRepo;
import gov.zndev.reviewlogclient.repositories.reviewlogs.ReviewLogsRepository;
import gov.zndev.reviewlogclient.repositories.users.UsersRepo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import java.util.Date;
import java.util.List;

@Component
@FxmlView("view_incident_layout.fxml")
public class ViewIncidentCtrl {

    @Autowired
    private FxWeaver fxWeaver;

    private IncidentRepository incidentRepo;
    private ReviewLogsRepository reviewRepo;
    private UsersRepo usersRepo;
    private PersonnelRepo personnelRepo;

    private Incident incident;
    private ReviewLog reviewLog;
    private User user;
    private Personnel personnel;

    private Stage stage;


    @FXML
    public void initialize() {
        incidentRepo = new IncidentRepository();
        reviewRepo = new ReviewLogsRepository();
        usersRepo = new UsersRepo();
        personnelRepo = new PersonnelRepo();
        setupView();
    }


    private void setupView() {

    }

    public void setData(Incident incident, ReviewLog reviewLog, Personnel personnel) {
        this.incident = incident;
        this.reviewLog = reviewLog;
        this.personnel = personnel;
        setIncidentDetails();
        setReviewLogDetails();
        setPersonnelDetails();
        usersRepo.getUserById(reviewLog.getReviewerId(), (success, message, object) -> {
            if (success) {
                List<User> users = (List<User>) object;
                if (!users.isEmpty()) {
                    user = users.get(0);
                    setUserDetails();
                }
            }
        });
        checkEditable();

    }

    private void setIncidentDetails() {
        date.setText(new SimpleDateFormat("MMMMM dd, yyyy").format(incident.getIncidentDate()));
        time.setText(new SimpleDateFormat("hh:mm:ss aaa").format(incident.getIncidentDate()));
        incident_number.setText(new DecimalFormat("000000").format(incident.getId()));

        // Load incident description to webview

        webview.getEngine().loadContent(incident.getDescription());
    }

    public void updateIncidentDateTime(LocalTime time, LocalDate date) {

        if (time != null) {
            incident.setTime(time.toString());
            incident.setDay(date.toString());
            LocalDateTime dt = LocalDateTime.of(date, time);
            incident.setIncidentDate(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()));
        } else {
            incident.setTime("");
            incident.setDay(date.toString());
            incident.setIncidentDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        incidentRepo.updateIncident(incident.getId(), incident, (success, message, object) -> {
            Platform.runLater(
                    () -> {
                        if (success) {
                            setIncidentDetails();
                            // updateReviewLogInclusiveDates();
                            Helper.UpdateReviewLogInclusiveDates(reviewLog, new Helper.HelperListener() {
                                @Override
                                public void UpdateReviewLogInclusiveDatesResult(boolean success, String message, Object obl) {
                                    listener.incidentUpdated();
                                }
                            });

                        } else {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Update Error", "Details not Updated!", "Error Occurred:\n" + message);
                        }
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "Update Successful", "Incident Details was Updated!", null);
                    }
            );
        });
    }


    private ViewIncidentListener listener;

    public void setListener(ViewIncidentListener listener) {
        this.listener = listener;
    }

    /*
    private void updateReviewLogInclusiveDates() {
        // Get Inclusive dates
        incidentRepo.getIncidentByReviewLog(reviewLog.getId(), (success, message, object) -> {
            if (success) {
                List<Incident> incidents = (List<Incident>) object;

                List<Date> dates = new ArrayList<>();
                for (Incident incident : incidents) {
                    dates.add(incident.getIncidentDate());
                }

                Collections.sort(dates);
                DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy");
                reviewLog.setInclusiveDates(dateFormat.format(dates.get(0)) + " - " + dateFormat.format(dates.get(dates.size() - 1)));

                reviewRepo.updateReviewLogInclusiveDates(reviewLog.getId(), reviewLog, new RepoInterface() {
                    @Override
                    public void activityDone(Boolean success, String message, Object object) {
                        if (success) {
                            System.out.println("Review Log was updated too.");
                        }
                    }
                });
            }
        });
    }

     */


    private void setReviewLogDetails() {
        review_id.setText(new DecimalFormat("000000").format(reviewLog.getId()));
    }

    private void setUserDetails() {
        added_by.setText(user.getFullname());
    }

    private void setPersonnelDetails() {
        personnel_name.setText(personnel.getFullName());
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }


    private void showEditDateTime() {
        boolean noTime = false;

        if (incident.getTime() == null) {
            noTime = true;
        } else if (incident.getTime().equals("")) {
            noTime = true;
        }

        FxControllerAndView<EditDateTimeCtrl, AnchorPane> editDateTime = fxWeaver.load(EditDateTimeCtrl.class);

        editDateTime.getController().setCtrl(this);
        editDateTime.getController().setIncident(incident, noTime);

        Stage stage = Helper.CreateStage("Edit Date&Time");
        stage.setScene(new Scene(editDateTime.getView().get()));

        editDateTime.getController().setStage(stage);

        stage.initOwner(this.stage);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }

    private void showDescriptionEditor() {
        FxControllerAndView<DescriptionEditorCtrl, AnchorPane> editor = fxWeaver.load(DescriptionEditorCtrl.class);

        Stage stage = Helper.CreateStage("Edit Description");
        stage.setScene(new Scene(editor.getView().get()));
        stage.initOwner(this.stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        editor.getController().setStage(stage);
        editor.getController().buildEditor(incident.getDescription());
        editor.getController().setEditorListener(description -> {
            updateDescription(description);
        });
        stage.show();

    }


    private void updateDescription(String description) {
        this.incident.setDescription(description);
        incidentRepo.updateIncident(incident.getId(), incident, (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {
                    AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "Update Success", "Description was updated!", null);
                    setIncidentDetails();
                });
            }
        });
    }

    private boolean isEditable = true;

    private void checkEditable() {
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
        btnEditDateTime.setVisible(false);
        btnEditDescription.setVisible(false);
    }

    @FXML
    private Button btnEditDescription;

    @FXML
    private Button btnEditDateTime;

    @FXML
    private WebView webview;

    @FXML
    private Label date;

    @FXML
    private Label time;

    @FXML
    private Label added_by;

    @FXML
    private Label personnel_name;

    @FXML
    private Label review_id;

    @FXML
    private Label incident_number;


    @FXML
    void onEditDescription(ActionEvent event) {
        showDescriptionEditor();
    }

    @FXML
    void onEditTimeAndDate(ActionEvent event) {
        showEditDateTime();
    }

    public interface ViewIncidentListener {
        void incidentUpdated();
    }

}
