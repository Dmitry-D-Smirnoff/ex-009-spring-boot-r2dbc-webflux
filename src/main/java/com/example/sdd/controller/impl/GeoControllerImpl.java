package com.example.sdd.controller.impl;

import com.example.sdd.controller.GeoController;
import com.example.sdd.dto.CountryDto;
import com.example.sdd.service.CountryService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeoControllerImpl implements GeoController {

    private final CountryService countryService;

    public GeoControllerImpl(CountryService countryService) {
        this.countryService = countryService;
    }

    public List<CountryDto> getAllCountries() {
        return countryService.getAllCountries();
    }

    public CountryDto getCountryById(Integer id) {
        return countryService.getCountryById(id);
    }

    public CountryDto createCountry(CountryDto countryDto) {
        return countryService.createCountry(countryDto);
    }

    public CountryDto updateCountry(Integer id, CountryDto countryDto) {
        return countryService.updateCountry(id, countryDto);
    }

    public void deleteCountry(Integer id) {
        countryService.deleteCountry(id);
    }
}