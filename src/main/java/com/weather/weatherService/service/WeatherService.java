package com.weather.weatherService.service;

import com.weather.weatherService.entity.Weather;
import com.weather.weatherService.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class WeatherService {

    private static final Logger log =
            LoggerFactory.getLogger(WeatherService.class);

    private final WeatherRepository weatherRepository;

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    // ================= GET WEATHER =================
    @Cacheable(value = "weather", key = "#city")
    public String getWeatherByCity(String city) {
        log.info("Fetching weather data for city={} (cache miss)", city);

        Optional<Weather> weather = weatherRepository.findByCity(city);

        if (weather.isPresent()) {
            log.info("Weather data found for city={}", city);
            return weather.get().getForecast();
        } else {
            log.warn("Weather data not found for city={}", city);
            return "Weather data not available";
        }
    }

    // ================= UPDATE WEATHER =================
    @CachePut(value = "weather", key = "#city")
    public String updateWeather(String city, String updatedWeather) {
        log.info("Updating weather for city={} with forecast={}", city, updatedWeather);

        weatherRepository.findByCity(city).ifPresentOrElse(weather -> {
            weather.setForecast(updatedWeather);
            weatherRepository.save(weather);
            log.info("Weather updated successfully for city={}", city);
        }, () -> {
            log.warn("No existing weather record found to update for city={}", city);
        });

        return updatedWeather;
    }

    // ================= DELETE WEATHER =================
    @Transactional
    @CacheEvict(value = "weather", key = "#city")
    public void deleteWeather(String city) {
        log.warn("Deleting weather data for city={}", city);

        weatherRepository.deleteByCity(city);

        log.warn("Weather data deleted and cache evicted for city={}", city);
    }
}
