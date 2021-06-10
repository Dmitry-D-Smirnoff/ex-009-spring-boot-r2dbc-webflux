package com.example.sdd.controller;

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

import java.util.List;

@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public interface GeoController {

    @GetMapping("/countries")
    List<CountryDto> getAllCountries();

    @GetMapping("/countries/{countryId}")
    CountryDto getCountryById(@PathVariable("countryId") Integer id);

    @PostMapping("/countries")
    @ResponseStatus(HttpStatus.CREATED)
    CountryDto createCountry(@RequestBody CountryDto countryDto);

    @PutMapping("/countries/{countryId}")
    CountryDto updateCountry(@PathVariable("countryId") Integer id, @RequestBody CountryDto countryDto);

    @DeleteMapping("/countries/{countryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCountry(@PathVariable("countryId") Integer id);
}