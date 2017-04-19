package hitec.com.util;

import hitec.com.ApplicationContext;

/**
 * Created by Arthur on 4/17/2017.
 */

public class URLManager {
    public static String getRegisterURL() {
        return ApplicationContext.HTTP_HOST + "/RegisterDevice.php";
    }

    public static String getUsersURL() {
        return ApplicationContext.HTTP_HOST + "/GetRelatedUsers.php";
    }

    public static String getSendNotificationURL() {
        return ApplicationContext.HTTP_HOST + "/sendSinglePush.php";
    }

    public static String getSendLocationURL() {
        return ApplicationContext.HTTP_HOST + "/sendLocation.php";
    }

    public static String getUserMessagesURL() {
        return ApplicationContext.HTTP_HOST + "/GetUserMessages.php";
    }

    public static String getLocationsURL() {
        return ApplicationContext.HTTP_HOST + "/GetLocations.php";
    }
}
