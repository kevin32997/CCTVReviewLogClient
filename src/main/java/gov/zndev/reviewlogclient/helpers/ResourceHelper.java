package gov.zndev.reviewlogclient.helpers;


import gov.zndev.reviewlogclient.models.User;

public class ResourceHelper {

    // App Title
    public static String APP_NAME = "ZN Inventory Client";

    // Main User ID
    public static User MAIN_USER = new User();


    // Server Keys

    public static final String CONFIG_KEY_LASTREVIEWID="review_log_last_id";

    // Table Update Keys

    public static final String TABLE_UPDATE_KEY_INCIDENTS="incident";
    public static final String TABLE_UPDATE_KEY_PERSONNEL="personnel";
    public static final String TABLE_UPDATE_KEY_REVIEW_LOG="review_log";
    public static final String TABLE_UPDATE_KEY_USERS="users";


    // Table Row Size
    public static int TABLE_ROW_SIZE=30; // Default



}
