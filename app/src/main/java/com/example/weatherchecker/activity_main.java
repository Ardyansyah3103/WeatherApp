package com.example.weatherchecker;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class activity_main extends AppCompatActivity{
    EditText etCityName, etCountryName;
    TextView tvResult, tvLocation, tvDesc, tvTemp;
    ImageView tvImage;

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY = "f4b50b1deba45469cbf0ce500a644cef";
    DecimalFormat df = new DecimalFormat("#.##");

    //private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCityName = findViewById(R.id.etCityName);
        etCountryName = findViewById(R.id.etCountryName);
        tvResult = findViewById(R.id.tvResult);
        tvImage = findViewById(R.id.tvImage);
        tvLocation = findViewById(R.id.tvLocation);
        tvDesc = findViewById(R.id.tvDesc);
        tvTemp  = findViewById(R.id.tvTemp);

    }

    // mekanisme call open Weather API data + show Output
    public void getWeatherDetails(View view) {
        String tempUrl = "";

        String city = etCityName.getText().toString().trim();
        String country = etCountryName.getText().toString().trim();

        if (city.equals("")) {
            tvResult.setText("City field can not be empty");
        } else {
            if (!country.equals("")) {
                tempUrl = url + "?q=" + city + "," + country + "&appid=" + API_KEY;
            } else {
                tempUrl = url + "?q=" + city + "&appid=" + API_KEY;
            }

            // Menambahkan timestamp agar selalu mendapat data terbaru
            tempUrl += "&_=" + System.currentTimeMillis();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, response -> {
                // tes response Logcat
                Log.d("response:", response);
                // berikan response disini!
                String output = "";

                // getting data variables and giving Output
                try {
                    JSONObject jsonresponse = new JSONObject(response);
                    JSONArray jsonArray = jsonresponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonresponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelslike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonresponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonresponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonresponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonresponse.getString("name");
                    tvResult.setTextColor(Color.rgb(68, 134, 199));
                    output += "current weather of " + cityName + "(" + countryName + ")"
                            + "\n Temp:" + df.format(temp) + " °C"
                            + "\n feels like: " + df.format(feelslike) + " °C"
                            + "\n Humidity: " + humidity + "%"
                            + "\n Description: " + description
                            + "\n Wind speed: " + wind + " m/s"
                            + "\n Cloudiness: " + clouds + "%"
                            + "\n Pressure: " + pressure + " hPa";
                    tvResult.setText(output);

                    // Logika if-else yang diperbarui
                    if (temp >= 30) {
                        tvImage.setImageResource(R.drawable.sunny1);
                        tvLocation.setText(cityName + ", " + countryName);
                        tvDesc.setText("Very Hot (" + description + ")");
                        tvTemp.setText(df.format(temp) + "°C");
                    } else if (temp >= 20 && temp < 30) {
                        tvImage.setImageResource(R.drawable.couldy2);
                        tvLocation.setText(cityName + ", " + countryName);
                        tvDesc.setText("Warm (" + description + ")");
                        tvTemp.setText(df.format(temp) + "°C");
                    } else if (temp >= 10 && temp < 20) {
                        tvImage.setImageResource(R.drawable.rainny1);
                        tvLocation.setText(cityName + ", " + countryName);
                        tvDesc.setText("Cool (" + description + ")");
                        tvTemp.setText(df.format(temp) + "°C");
                    } else if (temp >= 0 && temp < 10) {
                        tvImage.setImageResource(R.drawable.snowy1);
                        tvLocation.setText(cityName + ", " + countryName);
                        tvDesc.setText("Cold (" + description + ")");
                        tvTemp.setText(df.format(temp) + "°C");
                    } else {
                        tvImage.setImageResource(R.drawable.default1);
                        tvLocation.setText("Unknown");
                        tvDesc.setText("Ada kesalahan..");
                        tvTemp.setText("- -");
                    }

                    // method perbarui tampilan
                    tvImage.invalidate();
                    tvLocation.invalidate();
                    tvDesc.invalidate();
                    tvTemp.invalidate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText( getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity_main.this, "City Not Found", Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}
