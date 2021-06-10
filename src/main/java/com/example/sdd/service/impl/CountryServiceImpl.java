package com.example.sdd.service.impl;

import com.example.sdd.dto.CountryDto;
import com.example.sdd.entity.City;
import com.example.sdd.entity.Country;
import com.example.sdd.repository.CityRepository;
import com.example.sdd.repository.CountryRepository;
import com.example.sdd.service.CountryService;
import org.apache.commons.collections4.IterableUtils;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CountryServiceImpl implements CountryService {

    private static final ModelMapper mapper = new ModelMapper();

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository, CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    public Flux<CountryDto> getAllCountries() {
        return IterableUtils.toList(countryRepository.findAll()).stream().map(this::convert).collect(Collectors.toList());
    }

    public Mono<CountryDto> getCountryById(Integer id) {

        return convert(countryRepository.findById(id)
                .orElseThrow(() -> new DataRetrievalFailureException("Country Not Found")));
    }

    public Mono<CountryDto> createCountry(CountryDto countryDto) {

        Integer countryId = countryRepository.create(countryDto.getName());
        countryDto.setId(countryId);

        //TODO: Move to the method beginning! Here now - just to test transaction: rollback of new country creation
        for(City city : countryDto.getCities()){
            List<City> existingCities = cityRepository.findCityByName(city.getName());
            if(!CollectionUtils.isEmpty(existingCities)){
                throw new DuplicateKeyException("New country has existing cities");
            }
        }

        for(City city : countryDto.getCities()){
            city.setId(cityRepository.create(city.getName(), countryId));
            city.setCountryId(countryId);
        }

        return countryDto;
    }

    public Mono<CountryDto> updateCountry(Integer id, CountryDto countryDto) {
        countryRepository.findById(id)
                .orElseThrow(() -> new DataRetrievalFailureException("Country Not Found"));

        List<City> countryDtoCities = countryDto.getCities();

        List<String> newCityNames = countryDtoCities.stream().map(City::getName).collect(Collectors.toList());

        for(City city : cityRepository.findCityByCountryId(id)){
            if(!newCityNames.contains(city.getName())){
                cityRepository.delete(city);
            }
        }

        List<City> resultCityList = new ArrayList<>();
        for(City city : countryDtoCities){
            List<City> existingCities = cityRepository.findCityByName(city.getName());
            if(CollectionUtils.isEmpty(existingCities)){
                city.setId(cityRepository.create(city.getName(), id));
                city.setCountryId(id);
                resultCityList.add(city);
            }
            else{
                for(City existingCity : existingCities){
                    cityRepository.update(existingCity.getId(), city.getName(), id);
                    resultCityList.add(existingCity);
                }
            }
        }

        countryRepository.update(id, countryDto.getName());
        countryDto.setId(id);
        countryDto.setCities(resultCityList);

        return countryDto;
    }

    public Mono<Void> deleteCountry(Integer id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new DataRetrievalFailureException("Country Not Found"));

        for(City city : cityRepository.findCityByCountryId(id)){
            cityRepository.delete(city);
        }

        countryRepository.delete(country);
        return ???;
    }

    public CountryDto convert(Country country){
        CountryDto countryDto = mapper.map(country, CountryDto.class);

        countryDto.setCities(cityRepository.findCityByCountryId(country.getId()));

        return countryDto;
    }

}
