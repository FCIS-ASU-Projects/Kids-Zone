package com.example.kidszone;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {
    // as we are making a post request to post a data
    // so we are annotating it with post
    // and along with that we are passing a parameter as users
    @GET("app-age-rating")

    //on below line we are creating a method to post our data.
    Call<AppAgeRate> createPost(@Query("package_name") String package_name);
}
