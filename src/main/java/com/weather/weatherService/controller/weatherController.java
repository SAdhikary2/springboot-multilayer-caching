package com.weather.weatherService.controller;

import com.weather.weatherService.entity.Weather;
import com.weather.weatherService.repository.WeatherRepository;
import com.weather.weatherService.service.CacheInspectionService;
import com.weather.weatherService.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class weatherController {

    private static final Logger log =
            LoggerFactory.getLogger(weatherController.class);

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private CacheInspectionService cacheInspectionService;

    // ================= GET WEATHER =================
    @GetMapping
    public String getWeather(@RequestParam String city) {
        log.info("Received request to get weather for city={}", city);

        String result = weatherService.getWeatherByCity(city);

        log.info("Successfully fetched weather for city={}", city);
        return result;
    }

    // ================= ADD WEATHER =================
    @PostMapping
    public Weather addWeather(@RequestBody Weather weather) {
        log.info("Received request to add weather data for city={}", weather.getCity());

        Weather savedWeather = weatherRepository.save(weather);

        log.info("Weather data saved successfully for city={}", weather.getCity());
        return savedWeather;
    }

    // ================= GET ALL =================
    @GetMapping("/all")
    public List<Weather> getAllWeather() {
        log.info("Fetching all weather records");

        List<Weather> list = weatherRepository.findAll();

        log.info("Total weather records found={}", list.size());
        return list;
    }

    // ================= CACHE DATA =================
    @GetMapping("/cacheData")
    public void getCacheData() {
        log.debug("Inspecting cache contents for cacheName=weather");
        cacheInspectionService.printCacheContents("weather");
        log.debug("Cache inspection completed for cacheName=weather");
    }

    // ================= UPDATE WEATHER =================
    @PutMapping("/{city}")
    public String updateWeather(@PathVariable String city,
                                @RequestParam String weatherUpdate) {

        log.info("Updating weather for city={} with new value={}", city, weatherUpdate);

        String response = weatherService.updateWeather(city, weatherUpdate);

        log.info("Weather updated successfully for city={}", city);
        return response;
    }

    // ================= DELETE WEATHER =================
    @DeleteMapping("/{city}")
    public String deleteWeather(@PathVariable String city) {
        log.warn("Deleting weather data for city={}", city);

        weatherService.deleteWeather(city);

        log.warn("Weather data deleted and cache evicted for city={}", city);
        return "Weather data for " + city + " has been deleted and cache evicted.";
    }
}
