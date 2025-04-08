package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var locationHelper: LocationHelper
    private val API = "ad151e290c4ce24c13ef7b2849f25477"
private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding =ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        locationHelper = LocationHelper(this) { lat, lon ->
            fetchWeatherData(lat, lon)

        }
        locationHelper.getLastLocation()

        binding.swipeRefresh.postDelayed({
            if (binding.swipeRefresh.isRefreshing) {
                Toast.makeText(this, "Request timed out. Please try again.", Toast.LENGTH_SHORT).show()
                binding.swipeRefresh.isRefreshing = false
            }
        }, 5000) // 5 seconds timeout
    }



    private fun fetchWeatherData(lat: String, lon: String) {
        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(WeatherApi::class.java)

        api.getWeather(lat, lon, API).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { weatherData ->
                        updateUI(weatherData)
                        binding.swipeRefresh.isRefreshing=false
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load weather", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Response Code: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", "onFailure: ${t.message}")
                binding.swipeRefresh.isRefreshing = false
            }
        })
    }

    private fun updateUI(weatherData: WeatherResponse) {
        binding =ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.address.text = "${weatherData.name}, ${weatherData.sys.country}"
        binding.temp.text = "${weatherData.main.temp}°C"
        binding.status.text = weatherData.weather[0].description.replaceFirstChar { it.uppercase() }
        binding.tempMin.text = "Min Temp: ${weatherData.main.temp_min}°C"
        binding.tempMax.text = "Max Temp: ${weatherData.main.temp_max}°C"
        binding.wind.text = "${weatherData.wind.speed} m/s"
        binding.pressure.text = "${weatherData.main.pressure} hPa"
        binding.humidity.text = "${weatherData.main.humidity}%"

        val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        binding.updatedAt.text="updated at: ${dateFormat.format(Date(weatherData.dt * 1000))}"


        binding.sunrise.text = "${dateFormat.format(Date(weatherData.sys.sunrise * 1000))}"
        binding.sunset.text = "${ dateFormat.format(Date(weatherData.sys.sunset * 1000))}"

        binding.loader.visibility = View.GONE
        binding.mainContainer.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE


    }
}
