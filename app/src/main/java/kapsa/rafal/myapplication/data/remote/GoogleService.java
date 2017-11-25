package kapsa.rafal.myapplication.data.remote;

import kapsa.rafal.myapplication.data.model.GoogleAnswersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Rafal on 2017-11-23.
 */


public interface GoogleService {
    @GET("/maps/api/place/textsearch/json?")
    Call<GoogleAnswersResponse> getAnswers(
            @Query("query") String query,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("key") String key);
}
