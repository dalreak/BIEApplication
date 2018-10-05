package com.projectbie.toojs.bieapplication;
/*
*
*   이 클래스는 retrofit2를 활용해 json 을 https api 로 받아오는데 필요한 함수, 초기화 함수들이 있는 클래스입니다
*
 */


import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class ConClient {

        public ConClient(){}

        // Api요청시 Base Url주소
        public static final String BASE_URL = "https://thingspeak.com/";
        public static Retrofit retrofit = null;

        public static Retrofit getClient(){
            if(retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }

}

class InitClient{

        //Context context; 수정중

   /* public InitClient(Context contexted){
        context = contexted;
    }
*/
   List<Feed> exam;

   List<Feed> getjsondata(){
       return exam;
   }

   void setjsondata(List<Feed> data){
       exam = data;
   }

    public void getjsonData(Context context) {
       final Context temp=context;
        try {
            // 클라이언트 인스턴스 생성(기본 셋팅 된 Retrofit 객체)
            ConClient client = new ConClient();
            retrointerface service = client.getClient().create(retrointerface.class);

            // 작성한 인터페이스에 있는 getNowPlayingMovies메서드를 이용해 API요청
            Call<responsebody> jsonapi = service.getData("560016");
            // Callback 메서드 작성
            jsonapi.enqueue(new Callback<responsebody>(){
                // 요청 성공
                @Override
                public void onResponse(Call<responsebody> call, Response<responsebody> response) {
                    // resonse.body()는 MovieResponse 모델 객체를 반환
                    if(response.body() != null) {

                        //List<Feed> movieList = response.body().getFeeds();

                        //String a = movieList.toString();
                        //setjsondata(response.body().getFeeds());
                        Toast.makeText(temp, exam.get(0).getField1(), Toast.LENGTH_SHORT).show(); //수정중



                    }
                    else {
                        Toast.makeText(temp, "정보를 가져오지 못했습니다. \nAPI키를 확인해주세요.", Toast.LENGTH_SHORT).show(); //수정중
                    }

                }
                // 요청 실패
                @Override
                public void onFailure(Call<responsebody> call, Throwable t) {
                   Toast.makeText(temp, "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show(); //수정중
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

interface retrointerface {
    @GET("channels/{channelid}/feed.json")
    Call<responsebody> getData(@Path("channelid")String channelid);
}