package gov.zndev.reviewlogclient.controller;

import gov.zndev.reviewlogclient.controller.components.AddIncidentDescriptionCtrl;
import gov.zndev.reviewlogclient.controller.components.LoginCtrl;
import gov.zndev.reviewlogclient.controller.components.ReviewIncidentItemCtrl;
import gov.zndev.reviewlogclient.controller.personnel.ViewPersonnelCtrl;
import gov.zndev.reviewlogclient.controller.reviewlogs.AddNewPersonnelCtrl;
import gov.zndev.reviewlogclient.controller.reviewlogs.ConfirmReviewCtrl;
import gov.zndev.reviewlogclient.controller.reviewlogs.ViewReviewLogCtrl;
import gov.zndev.reviewlogclient.controller.users.ViewUserController;
import gov.zndev.reviewlogclient.helpers.*;
import gov.zndev.reviewlogclient.helpers.JasperViewer.JasperViewerFx;
import gov.zndev.reviewlogclient.models.Incident;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.ReviewLog;
import gov.zndev.reviewlogclient.models.User;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.models.other.Sort;
import gov.zndev.reviewlogclient.repositories.incidents.IncidentRepository;
import gov.zndev.reviewlogclient.repositories.personnels.PersonnelRepo;
import gov.zndev.reviewlogclient.repositories.reports.ReportsRepository;
import gov.zndev.reviewlogclient.repositories.reviewlogs.ReviewLogsRepository;
import gov.zndev.reviewlogclient.repositories.system.SystemDataRepository;
import gov.zndev.reviewlogclient.repositories.users.UsersRepo;
import gov.zndev.reviewlogclient.services.TableUpdate;
import gov.zndev.reviewlogclient.services.TableUpdateService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@FxmlView("main_layout.fxml")
@Component
public class MainController {

    @FXML
    private TabPane main_tabpane;

    @FXML
    private Tab tab_review_log;

    @FXML
    private Tab tab_personnel;

    @FXML
    private Tab tab_user;

    @FXML
    private Tab tab_settings;


    @FXML
    private AnchorPane rev_tab_pane;


    @FXML
    private TableView<ReviewLog> rev_table_logs;

    @FXML
    private TableColumn<ReviewLog, String> rev_col_id;

    @FXML
    private TableColumn<ReviewLog, String> rev_col_fullname;

    @FXML
    private TableColumn<ReviewLog, String> rev_col_office;

    @FXML
    private TableColumn<ReviewLog, String> rev_col_designation;

    @FXML
    private TableColumn<ReviewLog, String> rev_col_inclusivedates;

    @FXML
    private TableColumn<ReviewLog, String> rev_col_reviewdate;

    @FXML
    private TableColumn<ReviewLog, String> rev_col_reviewer;

    @FXML
    private TextField review_search_name;

    @FXML
    private TextField review_office;

    @FXML
    private ListView<AnchorPane> review_incident_list;


    @FXML
    private TextField review_search_logs;

    @FXML
    private Pagination rev_pagination;

    @FXML
    private TableView<Personnel> personnel_table;

    @FXML
    private TableColumn<Personnel, String> per_col_fullname;

    @FXML
    private TableColumn<Personnel, String> per_col_office;

    @FXML
    private TableColumn<Personnel, String> per_col_designation;

    @FXML
    private TableColumn<Personnel, String> per_col_reviews;

    @FXML
    private TableColumn<Personnel, String> per_col_dateadded;

    @FXML
    private TextField per_firsname;

    @FXML
    private TextField per_surname;

    @FXML
    private Button onPersonnelSave;

    @FXML
    private TextField per_search;

    @FXML
    private Pagination per_pagination;

    @FXML
    private TextField per_office;

    @FXML
    private TextField per_designation;


    @FXML
    private TextField per_middlename;

    @FXML
    private TableView<User> user_table;

    @FXML
    private TableColumn<User, String> user_col_fullname;

    @FXML
    private TableColumn<User, String> user_col_username;

    @FXML
    private TableColumn<User, String> user_col_reviewed;

    @FXML
    private TableColumn<User, String> user_col_datecreated;

    @FXML
    private TextField user_fullname;

    @FXML
    private TextField user_username;

    @FXML
    private TextField user_search;

    @FXML
    private Pagination user_pagination;

    @FXML
    private PasswordField user_password;

    @FXML
    private PasswordField user_confirm_pass;

    @FXML
    private ComboBox<String> user_userRole;

    @FXML
    private TextField review_rowsize_field;

    @FXML
    private TextField per_rowsize_field;

    @FXML
    private TextField user_rowsize_field;


    /*==================================================================================================================
                                                            MAIN
    ==================================================================================================================*/

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private Label main_user_label;

    // Repos
    private PersonnelRepo personnelRepo;
    private ReviewLogsRepository reviewLogsRepo;
    private UsersRepo usersRepo;
    private SystemDataRepository systemRepo;
    private ReportsRepository reportsRepo;
    private TableUpdateService updatesService;
    private IncidentRepository incidentRepo;

    @Autowired
    private FxWeaver fxWeaver;

    private Stage main_stage;


    @FXML
    public void initialize() {
        personnelRepo = new PersonnelRepo();
        reviewLogsRepo = new ReviewLogsRepository();
        usersRepo = new UsersRepo();
        systemRepo = new SystemDataRepository();
        reportsRepo = new ReportsRepository();
        updatesService = new TableUpdateService();
        incidentRepo = new IncidentRepository();
        setup();
    }

    private void setup() {
        // Other
        setTableRowSizes();
        setupTableRowSizeField();

        // Tabs
        setupPersonnelTab();
        setupReviewLogsTab();
        setupUsersTab();
        startAutoUpdate();
        setupReportsTab();
        setupSettingsTab();
    }

    private void setTableRowSizes() {
        review_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
        per_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
        user_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);

        review_table_row_size = ConfigProperties.TABLE_ROW_SIZE;
        per_table_row_size = ConfigProperties.TABLE_ROW_SIZE;
        user_table_row_size = ConfigProperties.TABLE_ROW_SIZE;
    }

    // Setups Row Size field Action on tabs w/ tables
    private void setupTableRowSizeField() {
        review_rowsize_field.setOnAction(e -> {
            log.info("Review row size field entered. Value: " + review_rowsize_field.getText());

            // Check if not empty
            if (!review_rowsize_field.getText().equals("")) {

                // Check if it is numeric
                try {
                    int new_rowsize = Integer.parseInt(review_rowsize_field.getText());
                    if ((new_rowsize > 0)) {

                        // Save the new Row Size
                        ConfigProperties.TABLE_ROW_SIZE = new_rowsize;

                        // Update config.properties
                        ConfigProperties.UpdateConfigurations();

                        // Update Row Fields on each tabs w/ tables
                        setTableRowSizes();

                        // Update Tables and Pagination
                        updateTablesAndPagination();
                    } else {
                        // Input cant be less than 1
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Input must be a number greater than 1!", null);
                        review_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
                    }
                } catch (NumberFormatException ex) {
                    // Show error
                    AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Input must be a number greater than 1!", null);
                    review_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                // Show alert
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Cant be empty!", "Input must be a number greater than 1.");
                review_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
            }
        });

        per_rowsize_field.setOnAction(e -> {
            log.info("Personnel row size field entered. Value: " + per_rowsize_field.getText());

            // Check if not empty
            if (!per_rowsize_field.getText().equals("")) {

                // Check if it is numeric
                try {
                    int new_rowsize = Integer.parseInt(per_rowsize_field.getText());
                    if ((new_rowsize > 0)) {

                        // Save the new Row Size
                        ConfigProperties.TABLE_ROW_SIZE = new_rowsize;

                        // Update config.properties
                        ConfigProperties.UpdateConfigurations();

                        // Update Row Fields on each tabs w/ tables
                        setTableRowSizes();

                        // Update Tables and Pagination
                        updateTablesAndPagination();
                    } else {
                        // Input cant be less than 1
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Input must be a number greater than 1!", null);
                        per_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
                    }
                } catch (NumberFormatException ex) {
                    // Show error
                    AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Input must be a number greater than 1!", null);
                    per_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                // Show alert
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Cant be empty!", "Input must be a number greater than 1.");
                per_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
            }
        });

        user_rowsize_field.setOnAction(e -> {
            log.info("User row size field entered. Value: " + user_rowsize_field.getText());

            // Check if not empty
            if (!user_rowsize_field.getText().equals("")) {

                // Check if it is numeric
                try {
                    int new_rowsize = Integer.parseInt(user_rowsize_field.getText());
                    if ((new_rowsize > 0)) {

                        // Save the new Row Size
                        ConfigProperties.TABLE_ROW_SIZE = new_rowsize;

                        // Update config.properties
                        ConfigProperties.UpdateConfigurations();

                        // Update Row Fields on each tabs w/ tables
                        setTableRowSizes();

                        // Update Tables and Pagination
                        updateTablesAndPagination();
                    } else {
                        // Input cant be less than 1
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Input must be a number greater than 1!", null);
                        user_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
                    }
                } catch (NumberFormatException ex) {
                    // Show error
                    AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Input must be a number greater than 1!", null);
                    user_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                // Show alert
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Input Error", "Cant be empty!", "Input must be a number greater than 1.");
                user_rowsize_field.setText("" + ConfigProperties.TABLE_ROW_SIZE);
            }
        });
    }

    private void updateTablesAndPagination() {
        refreshReviewLogTableData();
        refreshPersonnelTableData();
        refreshUsersTableData();
    }

    public void setUser(User user) {
        ResourceHelper.MAIN_USER = user;
        main_user_label.setText(user.getFullname());
        checkUserAuth(user);
    }

    private void checkUserAuth(User user) {
        if (user.getUserRole().equals(User.ROLE_ENCODER)) {
            main_tabpane.getTabs().remove(tab_user);
        }
    }

    public void setStage(Stage stage) {
        this.main_stage = stage;
        main_stage.setOnHiding(e -> {
            mainStageOnCloseEvent();
        });
    }

    private void mainStageOnCloseEvent() {
        closeAutoUpdates();
    }

    public void openMainUser() {
        openUser(ResourceHelper.MAIN_USER);
    }

    private void closeAutoUpdates() {
        log.info("Closing Threads . . .");
        runAutoUpdate = false;
        autoUpdateThread = null;
    }

    public void logout() {
        // Close running threads b4 logout
        closeAutoUpdates();

        // Init Login form
        FxControllerAndView<LoginCtrl, AnchorPane> loginForm = fxWeaver.load(LoginCtrl.class);

        loginForm.getController().setStage(main_stage);
        Scene scene = new Scene(loginForm.getView().get(), 306, 162);
        this.main_stage.setScene(scene);
        this.main_stage.setResizable(false);
        this.main_stage.centerOnScreen();

        // Clear user data
        ResourceHelper.MAIN_USER = null;
    }


    /*==================================================================================================================
                                                        REVIEW LOGS
    ==================================================================================================================*/
    private Personnel selectedPersonnel;

    private void setupReviewLogsTab() {
        setupReviewLogsField();
        setupReviewLogsTable();
        setupPersonnelSearchEngine();
        setupReviewLogPagination();
        loadReviewLogToTable(review_selected_page, review_table_row_size);

    }

    private void setupReviewLogsField() {

        // Search Function
        review_search_logs.setOnAction(e -> {
            String search = review_search_logs.getText();
            if (!search.equals("")) {
                try {

                    int searchAsId = Integer.parseInt(search);
                    log.info("Review Search is numeric.");
                    searchReviewById(searchAsId);
                    return;
                } catch (NumberFormatException ex) {
                    log.info("Cannot parse search to Integer.");
                }

                searchReviewByPersonnel(search);
            }
        });

        review_search_logs.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                loadReviewLogToTable(review_selected_page, review_table_row_size);
            }
        });

        review_incident_list.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Incident incident = incidents.get(review_incident_list.getSelectionModel().getSelectedIndex());
                boolean noTime = true;
                if (!incident.getTime().equals("")) {
                    noTime = false;
                }
                openAddIncident(incident.getIncidentDate(), noTime, incident.getDescription(), AddIncidentDescriptionCtrl.MODE_EDIT, review_incident_list.getSelectionModel().getSelectedIndex());
            }
        });
    }

    private void searchReviewByPersonnel(String search) {
        log.info("Searching by personnel's info . . .");
        reviewLogsRepo.searchReviewLogByPersonnel(search, review_table_row_size, (success, message, object) -> {
            if (success) {
                log.info("Search success.");
                List<ReviewLog> list = (List<ReviewLog>) object;
                log.info("Searched list size is " + list.size());

                Platform.runLater(() -> {
                    rev_table_logs.getItems().clear();
                    rev_table_logs.getItems().addAll(list);
                });

            } else {
                log.info("Error occurred while searching\n" + message);
            }
        });
    }

    private void searchReviewById(int id) {
        log.info("Searching by review log id . . .");
        reviewLogsRepo.searchReviewLogById(id, review_table_row_size, (success, message, object) -> {
            if (success) {
                List<ReviewLog> list = (List<ReviewLog>) object;

                Platform.runLater(() -> {
                    rev_table_logs.getItems().clear();
                    rev_table_logs.getItems().addAll((List<ReviewLog>) object);
                });

            } else {
                log.info("Error occurred while searching\n" + message);
            }
        });
    }

    private void setupReviewLogsTable() {
        // Table On Items clicked
        rev_table_logs.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openReviewLog(rev_table_logs.getSelectionModel().getSelectedItem());
            }
        });


        DecimalFormat df = new DecimalFormat("000000");
        DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa");

        this.rev_col_id.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty(df.format(c.getValue().getId()));
            }
            return new SimpleStringProperty("<no data>");
        });

        this.rev_col_fullname.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty("" + c.getValue().getPersonnel().getFullName());
            }
            return new SimpleStringProperty("<no data>");
        });

        this.rev_col_office.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty("" + c.getValue().getPersonnel().getOffice());
            }
            return new SimpleStringProperty("<no data>");
        });

        this.rev_col_designation.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty("" + c.getValue().getPersonnel().getPosition());
            }
            return new SimpleStringProperty("<no data>");
        });


        this.rev_col_inclusivedates.setCellValueFactory(new PropertyValueFactory<>("inclusiveDates"));


        this.rev_col_reviewdate.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty(dateFormat.format(c.getValue().getReviewDate()));
            }
            return new SimpleStringProperty("<no data>");
        });
        this.rev_col_reviewer.setCellValueFactory(new PropertyValueFactory<>("reviewerName"));

    }

    private void openReviewLog(ReviewLog reviewLog) {
        FxControllerAndView<ViewReviewLogCtrl, AnchorPane> reviewLogViewer = fxWeaver.load(ViewReviewLogCtrl.class);

        Stage stage = Helper.CreateStage("View Review");

        Scene scene = new Scene(reviewLogViewer.getView().get(), 930, 485);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(main_stage);

        reviewLogViewer.getController().setStage(stage);
        reviewLogViewer.getController().setReviewLog(reviewLog);

        stage.show();
    }

    private void setupPersonnelSearchEngine() {

        SearchResultListView rev_search_list = new SearchResultListView(review_search_name, 375, 27);

        review_search_name.setOnKeyReleased(keyEvent -> {

            if (keyEvent.getCode() == KeyCode.DOWN) {
                rev_search_list.requestFocus();
                rev_search_list.getSelectionModel().select(0);
                return;
            }

            if (!review_search_name.getText().equals("")) {
                personnelRepo.searchPersonnel(review_search_name.getText(), 10, (success, message, object) -> {
                    if (success) {
                        List<Personnel> personnelList = (List<Personnel>) object;
                        rev_search_list.setList(personnelList);
                        if (personnelList.size() > 0) {
                            List<String> stringList = new ArrayList<>();
                            for (Personnel personnel : personnelList) {
                                stringList.add(personnel.getFullName());
                            }
                            rev_search_list.displayResult(stringList);
                        } else {
                            rev_search_list.displayNoDataFound("No data found . . .");
                            rev_search_list.getList().clear();
                        }
                        rev_search_list.setVisible(true);
                    } else {
                        // Error code here
                        System.out.println("Error Occurred\n" + message);
                        rev_search_list.getList().clear();
                    }
                });
            } else {
                rev_search_list.setVisible(false);
                rev_search_list.setList(new ArrayList<>());
            }
        });

        review_search_name.setOnAction(e -> {
            if (rev_search_list.getItems().size() > 0) {
                if (!rev_search_list.getItems().get(0).equals("No data found . . .")) {

                    // setSelectedType((Type) rev_search_list.getList().get(rev_search_list.getSelectionModel().getSelectedIndex() + 1));
                    setReviewPersonnelData((Personnel) rev_search_list.getList().get(rev_search_list.getSelectionModel().getSelectedIndex() + 1));

                    rev_search_list.getList().clear();
                    rev_search_list.getItems().clear();
                    rev_search_list.setVisible(false);

                    review_incident_list.requestFocus();
                } else {

                    // Error message here no data found // Create new Brand
                    System.out.println("No data found, Cant use enter");
                    // Add New Personnel
                    reviewLogAddNewPersonnel(review_search_name.getText());
                    //createType(type.getText());
                    rev_search_list.getList().clear();
                    rev_search_list.getItems().clear();
                    rev_search_list.setVisible(false);
                }
            } else {
                System.out.println("List is empty");

                // Add New Personnel
                reviewLogAddNewPersonnel(review_search_name.getText());
                //createType(type.getText());
            }
        });

        rev_search_list.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {

                //setSelectedType((Type) rev_search_list.getList().get(rev_search_list.getSelectionModel().getSelectedIndex()));

                setReviewPersonnelData((Personnel) rev_search_list.getList().get(rev_search_list.getSelectionModel().getSelectedIndex()));

                rev_search_list.getList().clear();
                rev_search_list.getItems().clear();
                rev_search_list.setVisible(false);


            }
        });

        rev_search_list.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                if (rev_search_list.getList().size() > 0) {
                    setReviewPersonnelData((Personnel) rev_search_list.getList().get(rev_search_list.getSelectionModel().getSelectedIndex()));

                    rev_search_list.getList().clear();
                    rev_search_list.getItems().clear();
                    rev_search_list.setVisible(false);
                }
            }
        });


        // Add search result to main anchor pane
        rev_tab_pane.getChildren().add(rev_search_list);
    }

    public void setReviewPersonnelData(Personnel personnel) {
        this.selectedPersonnel = personnel;
        this.review_search_name.setText(personnel.getFullName());
        this.review_office.setText(personnel.getOffice());
    }

    private void openConfirmSubmitReview() {
        if (reviewValidateFields()) {
            ReviewLog reviewLog = new ReviewLog();

            reviewLog.setPersonnelId(selectedPersonnel.getId());
            reviewLog.setPersonnelName(selectedPersonnel.getFullName());
            reviewLog.setReviewerId(ResourceHelper.MAIN_USER.getId());
            reviewLog.setReviewerName(ResourceHelper.MAIN_USER.getFullname());
            reviewLog.setReviewerCodeName(ResourceHelper.MAIN_USER.getUsername());

            // Get Inclusive dates
            List<Date> dates = new ArrayList<>();
            for (Incident incident : incidents) {
                dates.add(incident.getIncidentDate());
            }

            Collections.sort(dates);
            DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy");
            reviewLog.setInclusiveDates(dateFormat.format(dates.get(0)) + " - " + dateFormat.format(dates.get(dates.size() - 1)));

            FxControllerAndView<ConfirmReviewCtrl, AnchorPane> confirmReviewModal = fxWeaver.load(ConfirmReviewCtrl.class);
            Stage stage = Helper.CreateStage("Confirm Review");
            Scene scene = new Scene(confirmReviewModal.getView().get(), 960, 790);
            stage.setScene(scene);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(main_stage);
            stage.setResizable(false);

            confirmReviewModal.getController().setStage(stage);
            confirmReviewModal.getController().setMainController(this);
            confirmReviewModal.getController().setReviewInfo(reviewLog, selectedPersonnel, incidents);

            stage.show();
        }
    }

    private List<Incident> incidents = new ArrayList<>();

    public void addIncident(Incident incident) {
        incidents.add(incident);
        FxControllerAndView<ReviewIncidentItemCtrl, AnchorPane> incItem = fxWeaver.load(ReviewIncidentItemCtrl.class);
        incItem.getController().getDate_time().setText(incident.getDay() + " " + incident.getTime());
        incItem.getController().getBtnRemove().setOnAction(e -> {
            review_incident_list.getItems().remove(incItem.getView().get());
            incidents.remove(incident);
        });
        // incItem.getController().getDescription().setText(incident.getDescription());
        review_incident_list.getItems().add(incItem.getView().get());
    }

    public void updateIncident(Incident updateData, int index) {
        Incident incident = incidents.get(index);
        incident.setIncidentDate(updateData.getIncidentDate());
        incident.setDay(updateData.getDay());
        incident.setTime(updateData.getTime());
        incident.setDescription(updateData.getDescription());

        AnchorPane pane = review_incident_list.getItems().get(index);
        Label date_time = (Label) pane.lookup("#date_time");
        date_time.setText(incident.getDay() + " " + incident.getTime());
    }


    private void openAddIncident(Date date, boolean noTime, String description, int mode, int list_index) {
        FxControllerAndView<AddIncidentDescriptionCtrl, AnchorPane> addIncidentModal = fxWeaver.load(AddIncidentDescriptionCtrl.class);
        Stage stage = Helper.CreateStage("Add Incident/Description");
        Scene scene = new Scene(addIncidentModal.getView().get(), 865, 670);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(main_stage);
        stage.setResizable(false);
        addIncidentModal.getController().setMainCtrl(this);
        addIncidentModal.getController().setStage(stage);
        addIncidentModal.getController().setData(date, noTime, description, mode, list_index);
        stage.show();
    }

    private boolean reviewValidateFields() {

        if (selectedPersonnel == null) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Please select a Personnel", null);
            review_search_name.requestFocus();
            return false;
        } else if (incidents.size() <= 0) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Please select add Incident to proceed", null);
            return false;
        }
        return true;
    }

    public void clearReviewFields() {
        review_search_name.clear();
        review_office.clear();
        selectedPersonnel = null;
        incidents.clear();
        review_incident_list.getItems().clear();
    }


    // Table ///////////////////////////////////////////////////////////////////////

    private int review_selected_page = 1;
    private int review_table_row_size = 35;
    private Sort review_sort = new Sort(Sort.DESCENDING, "reviewDate");

    private void setupReviewLogPagination() {
        reviewLogsRepo.getReviewLogCount((success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {
                    int item_count = (Integer) object;
                    int page_count = item_count / review_table_row_size;
                    if (item_count % review_table_row_size > 0) {
                        page_count++;
                    }
                    rev_pagination.setCurrentPageIndex(review_selected_page - 1);
                    rev_pagination.setMaxPageIndicatorCount(50);

                    rev_pagination.setPageCount(page_count);
                    rev_pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                        rev_pagination.setCurrentPageIndex(newIndex.intValue());
                        review_selected_page = newIndex.intValue() + 1;
                        loadReviewLogToTable(review_selected_page, review_table_row_size);
                    });
                });
            }
        });
    }

    private void loadReviewLogToTable(int page, int size) {

        reviewLogsRepo.getReviewsByPageSorted(page, size, review_sort, (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {
                    rev_table_logs.getItems().clear();
                    rev_table_logs.getItems().addAll((List<ReviewLog>) object);
                });
            } else {
                Platform.runLater(() -> {
                    AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Message", "Can't load Data", "An Error has occurred!\n" + message);
                });
            }
        });
    }

    private void refreshReviewLogTableData() {
        setupReviewLogPagination();
        loadReviewLogToTable(review_selected_page, review_table_row_size);
    }

    private void reviewLogAddNewPersonnel(String initName) {

        FxControllerAndView<AddNewPersonnelCtrl, AnchorPane> addPersonnel = fxWeaver.load(AddNewPersonnelCtrl.class);
        addPersonnel.getController().setCtrl(this);
        addPersonnel.getController().setInitName(initName);
        Stage stage = Helper.CreateStage("Create Personnel");
        addPersonnel.getController().setStage(stage);

        Scene scene = new Scene(addPersonnel.getView().get());
        stage.setScene(scene);
        stage.setResizable(false);

        stage.initOwner(this.main_stage);

        stage.show();
    }


    @FXML
    void onReviewProceed(ActionEvent event) {
        openConfirmSubmitReview();
    }

    @FXML
    void onReviewAdd(ActionEvent event) {
        openAddIncident(null, true, "", AddIncidentDescriptionCtrl.MODE_ADD, -1);
    }

    @FXML
    void onReviewAddNewPersonnel(ActionEvent event) {
        reviewLogAddNewPersonnel("");

    }


    /*==================================================================================================================
                                                           PERSONNEL
    ==================================================================================================================*/

    private void setupPersonnelTab() {
        setupPersonnelTable();
        setupPersonnelField();
        setupPersonnelPagination();

        loadPersonnelToTable(per_selected_page, per_table_row_size);
    }

    private void setupPersonnelField() {

        // Personnel Search functions
        per_search.setOnAction(e -> {
            String search = per_search.getText();
            if (!search.equals("")) {
                searchPersonnel(search);
            }
        });

        per_search.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                loadPersonnelToTable(per_selected_page, per_table_row_size);
            }
        });

        this.per_firsname.setOnAction(e -> {
            per_surname.requestFocus();
        });
        this.per_surname.setOnAction(e -> {
            per_middlename.requestFocus();
        });
        this.per_middlename.setOnAction(e -> {
            per_office.requestFocus();
        });
        this.per_office.setOnAction(e -> {
            per_designation.requestFocus();
        });

        this.per_designation.setOnAction(e -> {
            saveNewPersonnel();
        });
    }

    private void searchPersonnel(String search) {
        log.info("Searching personnel by personnel's info . . .");
        personnelRepo.searchPersonnel(search, per_table_row_size, (success, message, object) -> {
            if (success) {
                log.info("Search success.");
                List<Personnel> list = (List<Personnel>) object;
                log.info("Searched list size is " + list.size());
                Platform.runLater(() -> {
                    personnel_table.getItems().clear();
                    personnel_table.getItems().addAll(list);
                });

            } else {
                log.info("Error occurred while searching\n" + message);
            }
        });
    }

    private void setupPersonnelTable() {
        this.per_col_fullname.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty("" + c.getValue().getFullName());
            }
            return new SimpleStringProperty("<no data>");
        });

        this.per_col_office.setCellValueFactory(new PropertyValueFactory<>("office"));
        this.per_col_designation.setCellValueFactory(new PropertyValueFactory<>("position"));
        this.per_col_reviews.setCellValueFactory(new PropertyValueFactory<>("reviews"));
        this.per_col_dateadded.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa");

        this.per_col_dateadded.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty("" + dateFormat.format(c.getValue().getDateCreated()));
            }
            return new SimpleStringProperty("<no data>");
        });

        this.personnel_table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openPersonnel(personnel_table.getSelectionModel().getSelectedItem());

            }
        });
    }

    private void openPersonnel(Personnel personnel) {
        FxControllerAndView<ViewPersonnelCtrl, AnchorPane> viewUser = fxWeaver.load(ViewPersonnelCtrl.class);
        viewUser.getController().setPersonnel(personnel);

        Stage stage = Helper.CreateStage("View Personnel - " + personnel.getFullName());
        Scene scene = new Scene(viewUser.getView().get());


        stage.setScene(scene);
        stage.setResizable(false);

        viewUser.getController().setStage(stage);

        stage.show();

    }

    private void saveNewPersonnel() {
        if (validateSavePersonnel()) {
            Personnel personnel = new Personnel();
            personnel.setFirstName(this.per_firsname.getText());
            personnel.setLastName(this.per_surname.getText());
            personnel.setMiddleName(this.per_middlename.getText());
            personnel.setOffice(this.per_office.getText());
            personnel.setPosition(this.per_designation.getText());
            personnel.setAddedBy(ResourceHelper.MAIN_USER.getId());

            personnelRepo.createdPersonnel(personnel, new RepoInterface() {
                @Override
                public void activityDone(Boolean success, String message, Object object) {
                    if (success) {

                        Platform.runLater(() -> {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Information", "Added Successfully", null);
                            clearPersonnelFields();
                        });
                    }
                }
            });
        }
    }

    private int per_selected_page = 1;
    private int per_table_row_size = 35;
    private Sort per_sort = new Sort(Sort.DESCENDING, "lastName");

    private void setupPersonnelPagination() {
        personnelRepo.getPersonnelCount((success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {
                    int item_count = (Integer) object;
                    int page_count = item_count / per_table_row_size;
                    if (item_count % per_table_row_size > 0) {
                        page_count++;
                    }
                    per_pagination.setCurrentPageIndex(per_selected_page - 1);

                    per_pagination.setMaxPageIndicatorCount(50);

                    per_pagination.setPageCount(page_count);
                    per_pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                        per_pagination.setCurrentPageIndex(newIndex.intValue());
                        per_selected_page = newIndex.intValue() + 1;
                        loadPersonnelToTable(per_selected_page, per_table_row_size);
                    });
                });
            }
        });
    }

    private void loadPersonnelToTable(int page, int size) {
        personnelRepo.getPersonnelByPageSorted(page, size, per_sort, (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {
                    personnel_table.getItems().clear();
                    personnel_table.getItems().addAll((List<Personnel>) object);
                });

                for (Personnel per : (List<Personnel>) object) {
                    reviewLogsRepo.getReviewLogCountByPersonnelId(per.getId(), new RepoInterface() {
                        @Override
                        public void activityDone(Boolean success, String message, Object object) {
                            if (success) {
                                per.setReviews((int) object);
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

    private void refreshPersonnelTableData() {
        setupPersonnelPagination();
        loadPersonnelToTable(per_selected_page, per_table_row_size);
    }


    private boolean validateSavePersonnel() {
        if (per_firsname.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please enter First name!", null);
            per_firsname.requestFocus();
            return false;
        } else if (per_surname.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please enter Surname!", null);
            per_surname.requestFocus();
            return false;
        } else if (per_office.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please fill Office and Designation.", null);
            per_surname.requestFocus();
        } else if (per_designation.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please fill Office and Designation.", null);
            return false;
        }

        return true;
    }

    private void clearPersonnelFields() {
        this.per_firsname.clear();
        this.per_surname.clear();
        this.per_middlename.clear();
        this.per_office.clear();
        this.per_designation.clear();
    }


    @FXML
    void onPersonnelSave(ActionEvent event) {
        saveNewPersonnel();
    }

    /*==================================================================================================================
                                                    END OF PERSONNEL TAB
    ==================================================================================================================*/


    /*==================================================================================================================
                                                       USERS TAB
    ==================================================================================================================*/
    private void setupUsersTab() {
        setupUsersTable();
        setupUsersField();
        setupUsersPagination();
        loadUsersToTable(user_selected_page, user_table_row_size);
    }

    private void setupUsersField() {

        // Personnel Search functions
        user_search.setOnAction(e -> {
            String search = user_search.getText();
            if (!search.equals("")) {
                searchUser(search);
            }
        });

        user_search.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                loadUsersToTable(user_selected_page, user_table_row_size);
            }
        });

        this.user_fullname.setOnAction(e -> {
            user_username.requestFocus();
        });
        this.user_username.setOnAction(e -> {
            user_password.requestFocus();
        });
        this.user_password.setOnAction(e -> {
            user_confirm_pass.requestFocus();
        });
        this.user_confirm_pass.setOnAction(e -> {
            // User role
        });

        this.user_userRole.getItems().addAll(User.ROLE_ENCODER, User.ROLE_ADMIN);
        this.user_userRole.getSelectionModel().select(0);
    }

    private void searchUser(String search) {
        log.info("Searching users by users's info . . .");
        usersRepo.searchUser(search, user_table_row_size, (success, message, object) -> {
            if (success) {
                log.info("Search success.");
                List<User> list = (List<User>) object;
                log.info("Searched list size is " + list.size());
                Platform.runLater(() -> {
                    user_table.getItems().clear();
                    user_table.getItems().addAll(list);
                });
            } else {
                log.info("Error occurred while searching\n" + message);
            }
        });
    }

    private void setupUsersTable() {
        this.user_col_fullname.setCellValueFactory(new PropertyValueFactory<>("fullname"));

        this.user_col_username.setCellValueFactory(new PropertyValueFactory<>("username"));

        this.user_col_reviewed.setCellValueFactory(new PropertyValueFactory<>("reviewCount"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa");
        this.user_col_datecreated.setCellValueFactory(c -> {
            if (c.getValue() != null) {
                return new SimpleStringProperty("" + dateFormat.format(c.getValue().getDateCreated()));
            }
            return new SimpleStringProperty("<no data>");
        });

        this.user_table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openUser(user_table.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void openUser(User user) {
        FxControllerAndView<ViewUserController, AnchorPane> viewUser = fxWeaver.load(ViewUserController.class);
        viewUser.getController().setUser(user);
        viewUser.getController().setMainCtrl(this);


        Stage stage = Helper.CreateStage("View User - " + user.getFullname());
        Scene scene = new Scene(viewUser.getView().get());


        stage.setScene(scene);
        stage.setResizable(false);

        viewUser.getController().setStage(stage);

        stage.show();

    }


    private int user_selected_page = 1;
    private int user_table_row_size = 35;
    private Sort user_sort = new Sort(Sort.DESCENDING, "fullname");

    private void setupUsersPagination() {
        usersRepo.getUserCount((success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {

                    int item_count = (Integer) object;
                    int page_count = item_count / user_table_row_size;
                    if (item_count % user_table_row_size > 0) {
                        page_count++;
                    }
                    user_pagination.setCurrentPageIndex(user_selected_page - 1);

                    user_pagination.setMaxPageIndicatorCount(page_count);

                    user_pagination.setPageCount(page_count);
                    user_pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                        user_pagination.setCurrentPageIndex(newIndex.intValue());
                        user_selected_page = newIndex.intValue() + 1;
                        loadUsersToTable(user_selected_page, user_table_row_size);
                    });
                });
            } else {
            }
        });
    }

    private void loadUsersToTable(int page, int size) {

        usersRepo.getUserByPageSorted(page, size, user_sort, (success, message, object) -> {
            if (success) {
                Platform.runLater(() -> {
                    user_table.getItems().clear();
                    user_table.getItems().addAll((List<User>) object);
                });

                for (User user : (List<User>) object) {
                    reviewLogsRepo.getReviewLogCountByUserId(user.getId(), new RepoInterface() {
                        @Override
                        public void activityDone(Boolean success, String message, Object object) {
                            if (success) {
                                user.setReviewCount((Integer) object);
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

    private void refreshUsersTableData() {
        setupUsersPagination();
        loadUsersToTable(user_selected_page, user_table_row_size);
    }

    private void saveNewUser() {
        if (validateSaveUser()) {
            User user = new User();
            user.setFullname(user_fullname.getText());
            user.setUsername(user_username.getText());
            user.setPassword(user_password.getText());
            user.setUserRole(user_userRole.getValue());

            usersRepo.createUser(user, new RepoInterface() {
                @Override
                public void activityDone(Boolean success, String message, Object object) {
                    if (success) {

                        Platform.runLater(() -> {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Information", "Added Successfully", null);
                            clearUsersField();
                        });
                    }
                }
            });
        }
    }

    private boolean validateSaveUser() {
        if (user_fullname.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please enter Full name!", null);
            user_fullname.requestFocus();
            return false;
        } else if (user_username.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please enter Username!", null);
            user_username.requestFocus();
            return false;
        } else if (user_password.getText().equals("")) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Please Enter Password!.", null);
            user_password.requestFocus();
        } else if (!user_password.getText().equals(user_confirm_pass.getText())) {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Password did not match!.", null);
            user_confirm_pass.requestFocus();
            return false;
        }
        return true;
    }

    private void clearUsersField() {
        this.user_fullname.clear();
        this.user_username.clear();
        this.user_password.clear();
        this.user_confirm_pass.clear();
    }


    @FXML
    void onUserSave(ActionEvent event) {
        saveNewUser();
    }

    /*==================================================================================================================
                                                         END OF USERS TAB
    ==================================================================================================================*/

    /*==================================================================================================================
                                                         REPORTS TAB
    ==================================================================================================================*/

    @FXML
    private DatePicker report_select_dateRangeFrom;

    @FXML
    private DatePicker report_select_dateRangeUntil;

    @FXML
    private ComboBox<String> report_select_monthly;

    @FXML
    private HBox report_progress_monthly;


    @FXML
    private TextField report_monthly_year;


    @FXML
    private Label report_progressLabel_montly;

    @FXML
    private HBox report_progress_dateRange;

    @FXML
    private Label report_progressLabel_dateRange;


    private String[] months = {
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };

    private String defaultYear = "";

    private void setupReportsTab() {
        report_select_monthly.getItems().addAll(months);
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        defaultYear = String.valueOf(year);
        report_monthly_year.setPromptText("Enter year (default " + defaultYear + ")");
    }

    private void generateMonthlyReport(int month, int year) {
        report_progressLabel_montly.setText("fetching data from server . . .");
        report_progress_monthly.setVisible(true);

        // fetch data from server
        new Thread(() -> {

            reportsRepo.getReviewByMonthAndYear(month + 1, year, (success, message, object) -> {
                if (success) {
                    List<ReviewLog> list = (List<ReviewLog>) object;
                    if (!list.isEmpty()) {
                        Platform.runLater(() -> {
                            report_progressLabel_montly.setText("creating report . . .");
                        });

                        int count = 0;
                        for (ReviewLog reviewLog : list) {
                            count++;
                            reviewLog.setCount("" + count);
                            reviewLog.setReviewId(new DecimalFormat("000000").format(reviewLog.getId()));
                            reviewLog.setPersonnelName(reviewLog.getPersonnel().getFullName());
                            reviewLog.setOffice(reviewLog.getPersonnel().getOffice());
                            reviewLog.setFormattedReviewDate(new SimpleDateFormat("MMMMM dd, yyyy").format(reviewLog.getReviewDate()));
                            reviewLog.setIncidentCountString("" + ((int) Double.parseDouble(reviewLog.getIncidentCountString())));
                        }

                        Map<String, Object> map = new HashMap<>();
                        map.put("title", "CCTV Review Monthly Report");
                        map.put("date_range", "Report Month/Year: " + months[month] + " " + year);
                        map.put("date_generated", "Date: " + new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa").format(new Date()));
                        map.put("report_created_by", ResourceHelper.MAIN_USER.getFullname());
                        createAndShowReport("Date Range Report", map, list);

                        Platform.runLater(() -> {
                            report_progress_monthly.setVisible(false);
                        });

                    } else {
                        Platform.runLater(() -> {
                            report_progress_monthly.setVisible(false);
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "Reporting Message", "No Data found.", null);
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        report_progress_monthly.setVisible(false);
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "Server Error", message, null);
                    });
                }
            });
        }).start();
    }

    private void generateDateRangeReport(String dateFrom, String dateUntil) {
        report_progressLabel_dateRange.setText("fetching data from server . . .");
        report_progress_dateRange.setVisible(true);

        // fetch data from server
        new Thread(() -> {

            reportsRepo.getReviewsBetweenDates(dateFrom, dateUntil, (success, message, object) -> {
                if (success) {
                    List<ReviewLog> list = (List<ReviewLog>) object;
                    if (!list.isEmpty()) {
                        Platform.runLater(() -> {
                            report_progressLabel_dateRange.setText("creating report . . .");
                        });

                        int count = 0;
                        for (ReviewLog reviewLog : list) {
                            count++;
                            reviewLog.setCount("" + count);
                            reviewLog.setReviewId(new DecimalFormat("000000").format(reviewLog.getId()));
                            reviewLog.setPersonnelName(reviewLog.getPersonnel().getFullName());
                            reviewLog.setOffice(reviewLog.getPersonnel().getOffice());
                            reviewLog.setFormattedReviewDate(new SimpleDateFormat("MMMMM dd, yyyy").format(reviewLog.getReviewDate()));
                            reviewLog.setIncidentCountString("" + ((int) Double.parseDouble(reviewLog.getIncidentCountString())));
                        }

                        Map<String, Object> map = new HashMap<>();
                        map.put("title", "CCTV Review Date Range Report");
                        map.put("date_range", "Date Range: " + dateFrom + " to " + dateUntil);
                        map.put("date_generated", "Date: " + new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss aaa").format(new Date()));
                        map.put("report_created_by", ResourceHelper.MAIN_USER.getFullname());
                        createAndShowReport("Date Range Report", map, list);

                        Platform.runLater(() -> {
                            report_progress_dateRange.setVisible(false);
                        });

                    } else {
                        Platform.runLater(() -> {
                            report_progress_dateRange.setVisible(false);
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "Reporting Message", "No Data found between dates.", null);
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        report_progress_dateRange.setVisible(false);
                        AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "Server Error", message, null);
                    });
                }
            });
        }).start();
    }


    private void createAndShowReport(String title, Map<String, Object> map, List<ReviewLog> list) {
        try {
            JasperReport report = JasperCompileManager.compileReport("cttvreviewreport.jrxml");
            JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(list);
            JasperPrint jprint = JasperFillManager.fillReport(report, map, data);
            Platform.runLater(() -> {
                new JasperViewerFx().viewReport(title, jprint);
            });

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onGenerateMonthlyReport(ActionEvent event) {
        if (report_select_monthly.getValue() != null) {
            try {
                int year = 0;
                if (report_monthly_year.getText().equals("")) {
                    year = Integer.parseInt(defaultYear);
                } else {
                    year = Integer.parseInt(report_monthly_year.getText());
                }
                generateMonthlyReport(report_select_monthly.getSelectionModel().getSelectedIndex(), year);
            } catch (NumberFormatException ex) {
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Reporting Error", "Year format is not Valid!", "Example format: 2019, 2020, 2021");
            }
        } else {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Reporting Error", "Please select Month and Year!", null);
        }
    }

    @FXML
    void onGenerateDateRangeReport(ActionEvent event) {

        if (report_select_dateRangeFrom.getValue() != null && report_select_dateRangeUntil.getValue() != null) {
            LocalDate dateFromLocalDate = report_select_dateRangeFrom.getValue();
            LocalDate dateUntilLocalDate = report_select_dateRangeUntil.getValue();

            // Compare Dates
            if (dateUntilLocalDate.compareTo(dateFromLocalDate) >= 0) {
                generateDateRangeReport(dateFromLocalDate.toString(), dateUntilLocalDate.toString());
            } else {
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Reporting Error", "Incompatible Dates!", "DATE UNTIL must be greater than DATE FROM");
            }

        } else {
            // Date Range no date selected
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "Reporting Error", "Please select Date on both fields!", null);
        }

    }

    /*==================================================================================================================
                                                        SETTINGS TAB
    ==================================================================================================================*/

    @FXML
    private TextField settings_address;

    private void setupSettingsTab() {
        setupServerAddress();
    }

    private void setupServerAddress() {
        this.settings_address.setText(ConfigProperties.BASE_URL);
    }

    @FXML
    void onTestConnection(ActionEvent event) {
        if (!settings_address.getText().equals("")) {
            String address = settings_address.getText();
            if (!address.endsWith("/")) {
                address = address + "/";
            }
            systemRepo.testConnection(address, new RepoInterface() {
                @Override
                public void activityDone(Boolean success, String message, Object object) {
                    if (success) {
                        Platform.runLater(() -> {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Info", message, null);
                        });

                    }
                }
            });

        } else {
            Platform.runLater(() -> {
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Field cannot be empty", "Default: http://localhost:8080/");
            });

        }
    }

    @FXML
    void onSaveConfigurations(ActionEvent event) {

        if (!settings_address.getText().equals("")) {
            String address = settings_address.getText();
            if (!address.endsWith("/")) {
                address = address + "/";
            }
            File configFile = new File("config.properties");
            try {
                Properties props = new Properties();
                props.setProperty("base_url", address);
                FileWriter writer = new FileWriter(configFile);
                props.store(writer, "Server Settings");
                writer.close();
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Info", "Configuration Saved", "Must Restart to apply configurations");
            } catch (FileNotFoundException ex) {
                // file does not exist
            } catch (IOException ex) {
                // I/O error
            }
        } else {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Field cannot be empty", "Default: http://localhost:8080/");
            settings_address.setText(ConfigProperties.BASE_URL);
        }
    }


    /*==================================================================================================================
                                                    Auto Table Updates
    ==================================================================================================================*/

    Thread autoUpdateThread;
    private boolean runAutoUpdate = false;

    private void startAutoUpdate() {
        runAutoUpdate = true;
        if (autoUpdateThread == null) {
            autoUpdateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (runAutoUpdate) {
                        updatesService.getUpdates((message, status, list) -> {
                            if (status) {
                                updateTables(list);
                            }
                        });
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });
            autoUpdateThread.start();
        }

    }

    private String incident_last_update = "";
    private String personnel_last_update = "";
    private String review_log_last_update = "";
    private String users_last_update = "";

    private void updateTables(List<TableUpdate> list) {
        for (TableUpdate update : list) {
            switch (update.getTableName()) {


                case ResourceHelper.TABLE_UPDATE_KEY_INCIDENTS:
                    if (!incident_last_update.equals(update.getLastUpdate())) {
                        incident_last_update = update.getLastUpdate();

                        // Update Table Here

                    }
                    break;
                case ResourceHelper.TABLE_UPDATE_KEY_PERSONNEL:
                    if (!personnel_last_update.equals(update.getLastUpdate())) {
                        personnel_last_update = update.getLastUpdate();

                        // Update Table Here
                        refreshPersonnelTableData();
                    }
                    break;
                case ResourceHelper.TABLE_UPDATE_KEY_REVIEW_LOG:
                    if (!review_log_last_update.equals(update.getLastUpdate())) {
                        review_log_last_update = update.getLastUpdate();

                        // Update Table Here
                        refreshReviewLogTableData();
                    }
                    break;

                case ResourceHelper.TABLE_UPDATE_KEY_USERS:
                    if (!users_last_update.equals(update.getLastUpdate())) {
                        users_last_update = update.getLastUpdate();
                        // Update Table Here
                        refreshUsersTableData();
                    }
                    break;
            }
        }
    }

}
