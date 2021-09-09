package gov.zndev.reviewlogclient.controller.users;

import gov.zndev.reviewlogclient.controller.MainController;
import gov.zndev.reviewlogclient.controller.reviewlogs.ViewReviewLogCtrl;
import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import gov.zndev.reviewlogclient.models.ReviewLog;
import gov.zndev.reviewlogclient.models.User;
import gov.zndev.reviewlogclient.models.other.Sort;
import gov.zndev.reviewlogclient.repositories.reviewlogs.ReviewLogsRepository;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
@FxmlView("view_user_layout.fxml")
public class ViewUserController {

    @FXML
    private TextField fullname;

    @FXML
    private TextField username;

    @FXML
    private TextField role;

    @FXML
    private TextField date_created;

    @FXML
    private TableView<ReviewLog> review_table;

    @FXML
    private TableColumn<ReviewLog, String> cl_reviewId;

    @FXML
    private TableColumn<ReviewLog, String> cl_personnel;

    @FXML
    private TableColumn<ReviewLog, String> cl_reviewDate;

    @FXML
    private Pagination pagination;

    @FXML
    private Button btnLogout;

    ///////////////////////////////////////////////////////////////////////////////

    @Autowired
    private FxWeaver fxWeaver;

    private ReviewLogsRepository reviewRepo;

    private Stage stage;
    private User user;
    private MainController mainCtrl;

    @FXML
    public void initialize() {
        reviewRepo = new ReviewLogsRepository();
        setupTable();
    }

    private void checkUser() {
        if (user.getId() == ResourceHelper.MAIN_USER.getId()) {
            btnLogout.setVisible(true);
        } else {
            btnLogout.setVisible(false);
        }

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUser(User user) {
        this.user = user;
        populateFields();
        setupPagination();
        loadReviewsToTable(selected_page, table_row_size);
        checkUser();
    }

    public void setMainCtrl(MainController mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    private void populateFields() {
        this.fullname.setText(user.getFullname());
        this.username.setText(user.getUsername());
        this.role.setText(user.getUserRole().toUpperCase());
        this.date_created.setText(new SimpleDateFormat("MMMMM dd, yyyy").format(user.getDateCreated()));
    }


    private void setupTable() {
        this.review_table.setOnMouseClicked(e->{
            if(e.getClickCount()==2){
                openReviewLog(review_table.getSelectionModel().getSelectedItem());
            }
        });


        this.cl_reviewId.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty(new DecimalFormat("000000").format(c.getValue().getId()));
            }
            return new SimpleStringProperty("<no data>");
        });

        this.cl_personnel.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty(c.getValue().getPersonnel().getFullName());
            }
            return new SimpleStringProperty("<no data>");
        });


        this.cl_reviewDate.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty(new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa").format(c.getValue().getReviewDate()));
            }
            return new SimpleStringProperty("<no data>");
        });
    }

    private int selected_page = 1;
    private int table_row_size = 17;
    private Sort sort = new Sort(Sort.DESCENDING, "id");

    private void setupPagination() {
        reviewRepo.getReviewLogCountByUserId(user.getId(), (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {

                    int item_count = (Integer) object;
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

        reviewRepo.getReviewsByPageAndUserSorted(user.getId(), page, size, sort, (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {
                    review_table.getItems().clear();
                    review_table.getItems().addAll((List<ReviewLog>) object);
                });
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


    @FXML
    void onLogout(ActionEvent event) {
        mainCtrl.logout();
        stage.close();
    }


}
