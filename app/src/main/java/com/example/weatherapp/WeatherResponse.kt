package com.example.weatherapp

data class WeatherResponse(
    val name: String,
    val main: Main,
    val sys: Sys,
    val weather: List<Weather>,
    val wind: Wind,
    val dt: Long
)

data class Main(
    val temp: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Int,
    val humidity: Int
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Weather(
    val description: String
)

data class Wind(
    val speed: Float
)
