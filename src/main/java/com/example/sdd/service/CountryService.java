package com.example.sdd.service;

import com.example.sdd.dto.CountryDto;

import java.util.List;

public interface CountryService {

    List<CountryDto> getAllCountries();

    CountryDto getCountryById(Integer id);

    CountryDto createCountry(CountryDto countryDto);

    CountryDto updateCountry(Integer id, CountryDto countryDto);

    void deleteCountry(Integer id);

}
