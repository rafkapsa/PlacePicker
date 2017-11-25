package kapsa.rafal.myapplication.utils;

import kapsa.rafal.myapplication.data.remote.GoogleService;
import kapsa.rafal.myapplication.data.remote.RetrofitClient;

/**
 * Created by Rafal on 2017-11-23.
 */

public class ApiUtils {
    public static final String BASE_URL = "https://maps.googleapis.com/";

    public static GoogleService getGoogleService() {
        return RetrofitClient.getClient(BASE_URL).create(GoogleService.class);
    }
}
