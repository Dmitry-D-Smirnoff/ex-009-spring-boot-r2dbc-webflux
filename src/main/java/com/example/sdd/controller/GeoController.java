package com.example.sdd.controller;

import com.example.sdd.dto.CountryCreateUpdateDto;
import com.example.sdd.dto.CountryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public interface GeoController {

    @GetMapping("/countries")
    Flux<CountryDto> getAllCountries();

    @GetMapping("/countries/{countryId}")
    Mono<CountryDto> getCountryById(@PathVariable("countryId") Integer id);

    @PostMapping("/countries")
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Mono<CountryDto>> createCountry(@RequestBody CountryCreateUpdateDto countryDto);

    @PutMapping("/countries/{countryId}")
    Mono<CountryDto> updateCountry(@PathVariable("countryId") Integer id, @RequestBody CountryCreateUpdateDto countryDto);

    @DeleteMapping("/countries/{countryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> deleteCountry(@PathVariable("countryId") Integer id);
}