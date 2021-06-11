package com.example.sdd.service;

import com.example.sdd.dto.CountryCreateUpdateDto;
import com.example.sdd.dto.CountryDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CountryService {

    Flux<CountryDto> getAllCountries();

    Mono<CountryDto> getCountryById(Integer id);

    Mono<Mono<CountryDto>> createCountry(CountryCreateUpdateDto countryDto);

    Mono<CountryDto> updateCountry(Integer id, CountryCreateUpdateDto countryDto);

    Mono<Void> deleteCountry(Integer id);

}
