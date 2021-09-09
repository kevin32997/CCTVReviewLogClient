package gov.zndev.reviewlogclient.controller.personnel;

import gov.zndev.reviewlogclient.controller.MainController;
import gov.zndev.reviewlogclient.controller.reviewlogs.ViewReviewLogCtrl;
import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.ReviewLog;
import gov.zndev.reviewlogclient.models.User;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.models.other.Sort;
import gov.zndev.reviewlogclient.repositories.incidents.IncidentRepository;
import gov.zndev.reviewlogclient.repositories.reviewlogs.ReviewLogsRepository;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
@FxmlView("view_personnel_layout.fxml")
public class ViewPersonnelCtrl {

    @FXML
    private TextField fullname;

    @FXML
    private TextField office;

    @FXML
    private TextField designation;

    @FXML
    private TextField date_created;

    @FXML
    private TableView<ReviewLog> review_table;

    @FXML
    private TableColumn<ReviewLog, String> cl_reviewId;

    @FXML
    private TableColumn<ReviewLog, String> cl_incidents;

    @FXML
    private TableColumn<ReviewLog, String> cl_reviewDate;

    @FXML
    private TableColumn<ReviewLog, String> cl_reviewer;

    @FXML
    private Pagination pagination;

    @FXML
    private TextField reviews;

    //////////////////////////////////////////////////////////////////////////


    private ReviewLogsRepository reviewRepo;
    private IncidentRepository incidentRepo;

    @Autowired
    private FxWeaver fxWeaver;

    private Stage stage;
    private Personnel personnel;


    @FXML
    public void initialize() {
        reviewRepo = new ReviewLogsRepository();
        incidentRepo = new IncidentRepository();
        setupTable();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
        populateFields();
        setupPagination();
        loadReviewsToTable(selected_page, table_row_size);
    }

    private void populateFields() {
        this.fullname.setText(personnel.getFullName());
        this.designation.setText(personnel.getPosition());
        this.office.setText(personnel.getOffice());
        this.date_created.setText(new SimpleDateFormat("MMMMM dd, yyyy").format(personnel.getDateCreated()));
    }


    private void setupTable() {

        this.review_table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openReviewLog(review_table.getSelectionModel().getSelectedItem());
            }
        });

        this.cl_reviewId.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty(new DecimalFormat("000000").format(c.getValue().getId()));
            }
            return new SimpleStringProperty("<no data>");
        });

        this.cl_incidents.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty("" + c.getValue().getIncidentCount());
            }
            return new SimpleStringProperty("<no data>");
        });


        this.cl_reviewDate.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty(new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa").format(c.getValue().getReviewDate()));
            }
            return new SimpleStringProperty("<no data>");
        });

        this.cl_reviewer.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty(c.getValue().getReviewerName());
            }
            return new SimpleStringProperty("<no data>");
        });
    }

    private int selected_page = 1;
    private int table_row_size = 17;
    private Sort sort = new Sort(Sort.DESCENDING, "id");

    private void setupPagination() {
        reviewRepo.getReviewLogCountByPersonnelId(personnel.getId(), (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {

                    int item_count = (Integer) object;
                    reviews.setText("" + item_count);

                    if (item_count > 0) {
                        int page_count = item_count / table_row_size;
                        if (item_count % table_row_size > 0) {
                            page_count++;
                        }
                        pagination.setCurrentPageIndex(selected_page - 1);

                        if (selected_page < 50) {
                            pagination.setMaxPageIndicatorCount(page_count);
                        } else {
                            pagination.setMaxPageIndicatorCount(50);
                        }
                        pagination.setPageCount(page_count);
                        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                            pagination.setCurrentPageIndex(newIndex.intValue());
                            selected_page = newIndex.intValue() + 1;
                            loadReviewsToTable(selected_page, table_row_size);
                        });
                    } else {
                        pagination.setVisible(false);
                    }
                });

            }
        });
    }


    private void loadReviewsToTable(int page, int size) {

        reviewRepo.getReviewsByPageAndPersonnelSorted(personnel.getId(), page, size, sort, (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {
                    review_table.getItems().clear();
                    review_table.getItems().addAll((List<ReviewLog>) object);
                });

                for (ReviewLog reviewLog : (List<ReviewLog>) object) {
                    incidentRepo.getIncidentCountByReviewId(reviewLog.getId(), new RepoInterface() {
                        @Override
                        public void activityDone(Boolean success, String message, Object object) {
                            if (success) {
                                reviewLog.setIncidentCount((Integer) object);
                            }
                        }
                    });
                }
            } else {
                Platform.runLater(() -> {
                    AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Message", "Can't load Data", "An Error has occurred!\n" + message);
                });
            }
        });
    }


    private void openReviewLog(ReviewLog reviewLog) {
        FxControllerAndView<ViewReviewLogCtrl, AnchorPane> reviewLogViewer = fxWeaver.load(ViewReviewLogCtrl.class);

        Stage stage = Helper.CreateStage("View Review");

        Scene scene = new Scene(reviewLogViewer.getView().get(), 930, 485);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(this.stage);

        reviewLogViewer.getController().setStage(stage);
        reviewLogViewer.getController().setReviewLog(reviewLog);

        stage.show();
    }

    private void openUpdateView() {
        FxControllerAndView<UpdatePersonnelCtrl, AnchorPane> updateView = fxWeaver.load(UpdatePersonnelCtrl.class);
        updateView.getController().setPersonnel(this.personnel);
        updateView.getController().setCtrl(this);

        Stage stage = Helper.CreateStage("Update Personnel");

        stage.setScene(new Scene(updateView.getView().get()));

        stage.setResizable(false);
        stage.initOwner(this.stage);


        updateView.getController().setStage(stage);

        stage.show();
    }

    @FXML
    void onUpdate(ActionEvent event) {
        openUpdateView();
    }
}
