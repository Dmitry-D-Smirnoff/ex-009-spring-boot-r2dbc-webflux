package com.example.sdd.controller.impl;

import com.example.sdd.controller.GeoController;
import com.example.sdd.dto.CountryCreateUpdateDto;
import com.example.sdd.dto.CountryDto;
import com.example.sdd.service.CountryService;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class GeoControllerImpl implements GeoController {

    private final CountryService countryService;

    public GeoControllerImpl(CountryService countryService) {
        this.countryService = countryService;
    }

    public Flux<CountryDto> getAllCountries() {
        return countryService.getAllCountries();
    }

    public Mono<CountryDto> getCountryById(Integer id) {
        return countryService.getCountryById(id);
    }

    public Mono<Mono<CountryDto>> createCountry(CountryCreateUpdateDto countryDto) {
        return countryService.createCountry(countryDto);
    }

    public Mono<CountryDto> updateCountry(Integer id, CountryCreateUpdateDto countryDto) {
        return countryService.updateCountry(id, countryDto);
    }

    public Mono<Void> deleteCountry(Integer id) {
        return countryService.deleteCountry(id);
    }
}