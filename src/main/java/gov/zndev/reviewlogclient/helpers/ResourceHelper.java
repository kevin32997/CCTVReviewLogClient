package gov.zndev.reviewlogclient.helpers;


import gov.zndev.reviewlogclient.models.User;

public class ResourceHelper {

    // App Title
    public static String APP_NAME = "ZN Inventory Client";

    // Main User ID
    public static User MAIN_USER = new User();

    // URL (Rest Api Connections)
    public static String BASE_URL = "http://10.10.100.2:84/";
    public static String DEFAULT_BASE_URL="http://10.10.100.2:84/";

    // CKEditor URL
    public static String EDITOR_URL = "http://localhost/editor/ckeditor/samples/index.html";
    public static String DEFAULT_EDITOR_URL = "http://localhost/editor/ckeditor/samples/index.html";

    // Configuration Keys

    public static final String CONFIG_KEY_LASTREVIEWID="review_log_last_id";

    // Table Update Keys

    public static final String TABLE_UPDATE_KEY_INCIDENTS="incident";
    public static final String TABLE_UPDATE_KEY_PERSONNEL="personnel";
    public static final String TABLE_UPDATE_KEY_REVIEW_LOG="review_log";
    public static final String TABLE_UPDATE_KEY_USERS="users";



}
