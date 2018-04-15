package com.example.camil.tabletapp.data.remote;
import com.example.camil.tabletapp.model.Post;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({"Content-Type: application/json", "Authorization:key=AAAATdnV6AI:APA91bFS76t3CctX-4KvTI7bff-N_yjmSGG48-WPOfI2YqZOdm5-tsjJlZcFG8XZF9M8B3Zcs6a2xatctecV2-cPP5RSAl3XO5J8gtYYbR0BnaHGqfPBl0sS5KUxCNkQOOSNDTLofh-9"})
    @POST("fcm/send")
    //@POST("t/8dqq5-1523779989/post")
    Call<Post> savePost(@Body Post post);
}
