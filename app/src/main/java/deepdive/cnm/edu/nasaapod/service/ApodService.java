package deepdive.cnm.edu.nasaapod.service;

import deepdive.cnm.edu.nasaapod.model.Apod;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApodService {

  @GET("planetary/apod")
  Call<Apod> get(@Query("api_key") String apiKey, @Query("date") String date);

}
