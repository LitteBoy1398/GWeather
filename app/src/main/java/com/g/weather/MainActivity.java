package com.g.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androdocs.httprequest.HttpRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String KOTA = "Jakarta";
    String API = "4952b7352a8cfcb792c97669f1788569";

    TextView KotaTxt, TanggalTxt, statusTxt, SuhuTxt;
    EditText KotakPencarian;
    Button TombolCari;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KotaTxt = findViewById(R.id.kota);
        TanggalTxt = findViewById(R.id.tanggal);
        statusTxt = findViewById(R.id.status);
        SuhuTxt = findViewById(R.id.suhu);
        KotakPencarian = findViewById(R.id.KotakPencarian);
        TombolCari = findViewById(R.id.TombolCari);


        new weatherTask().execute();

        TombolCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KOTA = KotakPencarian.getText().toString();
                new weatherTask().execute();
            }
        });
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.Container).setVisibility(View.GONE);
            findViewById(R.id.textError).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + KOTA + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                String weatherDescription = weather.getString("description");
                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                KotaTxt.setText(address);
                TanggalTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                SuhuTxt.setText(temp);

                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.Container).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.textError).setVisibility(View.VISIBLE);
            }

        }
    }
}
