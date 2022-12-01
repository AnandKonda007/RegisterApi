package com.example.apiregister;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitController {
    private static RetroFitController retroFitController;
    private static Retrofit retrofit = null;
    private Context context;

    private static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Api.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static RetroFitController getInstance() {
        if (retroFitController == null) {
            retroFitController = new RetroFitController();
        }
        return retroFitController;
    }

    public void fillcontext(Context context) {
        this.context = context;
    }

    public boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkCapabilities capabilities = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR");
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI");
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET");
                    return true;
                }
            }
        }
        return false;
    }

    public void ApiCallbacksForAllPosts(Context context, String EndUrl, JsonObject jsonobj) {
        this.context = context;
        Api api = getClient().create(Api.class);
        Call<ResponseBody> call = api.RegisterApi(jsonobj, EndUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String Body = null;
                if (response.body() != null) {
                    try {
                        Body = new String(response.body().bytes());
                        //Log.e(" ",Body);
                        EventBus.getDefault().post(new MessageEvent(Body));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new MessageEvent(Body));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                EventBus.getDefault().post(new MessageEvent(null));


                Toast.makeText(context, ":" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public static class MessageEvent {
        public String body;

        public MessageEvent(String body) {
            this.body = body;

        }
    }
}