package com.example.apiregister;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apiregister.ServerResponseModels.RegisterResponseModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText name, email, phonenumber, bankname, accountnumber, ifsccode, accountname, password;
    Button Register;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Registration under process please wait....");
        actions();
        RetroFitController.getInstance().fillcontext(MainActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(MainActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(MainActivity.this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RetroFitController.MessageEvent messageEvent) {
        progressDialog.dismiss();
        Log.e("response", "call" + messageEvent.body);
            if (messageEvent.body != null) {
                try {
                    JSONObject jObj = new JSONObject(messageEvent.body);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
                Gson gson = new Gson();
                RegisterResponseModel registerResponseModel = gson.fromJson(messageEvent.body, RegisterResponseModel.class);
                if (registerResponseModel.getResponse()==3) {
                    Log.e("register Response", "call" + registerResponseModel.getResponse());

                    Toast.makeText(getApplicationContext(), registerResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                    action();
                } else {
                    Toast.makeText(getApplicationContext(), registerResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    public void action() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }


    private void actions() {
        name = findViewById(R.id.Name);
        email = findViewById(R.id.userId);
        phonenumber = findViewById(R.id.PhoneNumber);
        bankname = findViewById(R.id.BankName);
        accountnumber = findViewById(R.id.AccountNumber);
        ifsccode = findViewById(R.id.IFSCCode);
        accountname = findViewById(R.id.AccountName);
        password = findViewById(R.id.Password);
        Register = findViewById(R.id.register);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty() ||
                        phonenumber.getText().toString().isEmpty() || bankname.getText().toString().isEmpty() ||
                        accountnumber.getText().toString().isEmpty() || ifsccode.getText().toString().isEmpty() ||
                        accountname.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter Required Fields", Toast.LENGTH_SHORT).show();

                } else {
                    if (RetroFitController.getInstance().checkNetwork()) {
                        progressDialog.show();
                        LoadRegisterApiParams();
                    } else {
                        Toast.makeText(MainActivity.this, "Please Check Your Internet Connection.", Toast.LENGTH_LONG).show();
                    }

                }


            }
        });


    }

    public void LoadRegisterApiParams() {
        JsonObject CheckUserObj = new JsonObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", name.getText().toString());
            jsonObject.put("userID", email.getText().toString());
            jsonObject.put("PhoneNumber", phonenumber.getText().toString());
            jsonObject.put("BankName", bankname.getText().toString());
            jsonObject.put("AccountNumber", accountnumber.getText().toString());
            jsonObject.put("IFSCCode", ifsccode.getText().toString());
            jsonObject.put("AccountName", accountname.getText().toString());
            jsonObject.put("Password", password.getText().toString());
            JsonParser jsonParser = new JsonParser();
            CheckUserObj = (JsonObject) jsonParser.parse(jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetroFitController.getInstance().ApiCallbacksForAllPosts(MainActivity.this, "seller/SellerRegister", CheckUserObj);

    }
}