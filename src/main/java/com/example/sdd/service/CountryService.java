package com.example.sdd.service;

import com.example.sdd.dto.CountryDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CountryService {

    Flux<CountryDto> getAllCountries();

    Mono<CountryDto> getCountryById(Integer id);

    Mono<CountryDto> createCountry(CountryDto countryDto);

    Mono<CountryDto> updateCountry(Integer id, CountryDto countryDto);

    Mono<Void> deleteCountry(Integer id);

}
