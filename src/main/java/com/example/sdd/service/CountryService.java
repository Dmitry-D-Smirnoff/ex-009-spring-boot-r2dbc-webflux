package com.example.sdd.service;

import com.example.sdd.dto.CountryCreateUpdateDto;
import com.example.sdd.dto.CountryDto;
import com.example.sdd.entity.City;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CountryService {

    Flux<City> getAllCities();

    Flux<CountryDto> getAllCountries();

    Mono<CountryDto> getCountryById(Integer id);

    Mono<Integer> createCountry(CountryCreateUpdateDto countryDto);

    Mono<CountryDto> updateCountry(Integer id, CountryCreateUpdateDto countryDto);

    Mono<Void> deleteCountry(Integer id);

}
