package com.example.sdd.service.impl;

import com.example.sdd.dto.CountryCreateUpdateDto;
import com.example.sdd.dto.CountryDto;
import com.example.sdd.entity.City;
import com.example.sdd.entity.Country;
import com.example.sdd.repository.CityRepository;
import com.example.sdd.repository.CountryRepository;
import com.example.sdd.service.CountryService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

//@Transactional
@Service
public class CountryServiceImpl implements CountryService {

    private static final ModelMapper mapper = new ModelMapper();

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository, CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    public Flux<City> getAllCities(){
        return cityRepository
                .findAll();
    }

    public Flux<CountryDto> getAllCountries() {
        return countryRepository
                .findAll()
                .map(this::convert);
    }

    public Mono<CountryDto> getCountryById(Integer id) {

        // .flatMap(country -> Mono.just(convert(country))) тоже самое что и .map(country -> convert(country))
        return countryRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new DataRetrievalFailureException("Country not found by id")))
                .map(this::convert);
    }

    public Mono<City> createCity(String name, Integer countryId) {
        return cityRepository
                .findById(
                        cityRepository
                                .create(name, countryId)
                                .switchIfEmpty(Mono.error(new DataIntegrityViolationException("City creation failed")))
                ).switchIfEmpty(Mono.error(new DataIntegrityViolationException("Created city not found")));
    }

    public Mono<Integer> createCountry(CountryCreateUpdateDto countryDto) {
        return countryRepository
                .create(countryDto.getName())
                .switchIfEmpty(Mono.error(new DataIntegrityViolationException("Country creation failed")))
                .doOnSuccess(id -> {
                            System.out.println("new id=" + id);
                            for(City city : countryDto.getCities()) {
                                cityRepository
                                        .findCityByName(city.getName())
                                        .doOnNext(existingCity -> {
                                            throw new DuplicateKeyException("New country has existing cities");
                                        });
                                createCity(city.getName(), id)
                                        .subscribe(savedCity -> {
                                            if (savedCity != null) {
                                                city.setId(savedCity.getId());
                                                city.setCountryId(id);
                                            } else {
                                                throw new DataAccessResourceFailureException("New city was not created");
                                            }
                                        });
                            }
                })
        ;
    }

    public Mono<CountryDto> updateCountry(Integer id, CountryCreateUpdateDto countryDto) {

        return countryRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new DataRetrievalFailureException("Country not found by id")))
                .handle((country, countrySynchronousSink) -> {
                    List<String> newCityNames = countryDto.getCities().stream().map(City::getName).collect(Collectors.toList());

                    cityRepository
                            .findCityByCountryId(id)
                            .filter(city -> !newCityNames.contains(city.getName()))
                            .doOnNext(city ->
                                    cityRepository
                                            .delete(city)
                                            .subscribe()
                            )
                            .subscribe();

                    for(City city : countryDto.getCities()){
                        cityRepository
                                .findCityByName(city.getName())
                                .collect(Collectors.summingInt(existingCity -> 1))
                                .subscribe(count -> {
                                    if (count == 0){
                                        cityRepository
                                                .create(city.getName(), id)
                                                .subscribe();
                                    }
                                });
                    }

                    countryRepository
                            .update(id, countryDto.getName())
                            .subscribe();
                    country.setName(countryDto.getName());

                    countrySynchronousSink.next(country);
                })
                .map(country -> convert((Country) country));
    }

    public Mono<Void> deleteCountry(Integer id) {
        return countryRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new DataRetrievalFailureException("Country Not Found")))
                .handle((country, synchronousSink) -> {
                    cityRepository
                            .findCityByCountryId(id)
                            .doOnNext(city ->
                                cityRepository
                                        .delete(city)
                                        .subscribe()
                            )
                            .subscribe();
                    countryRepository.delete(country).subscribe();
                });
    }

    public CountryDto convert(Country country){
        CountryDto countryDto = mapper.map(country, CountryDto.class);
        countryDto.setCities(cityRepository.findCityByCountryId(country.getId()));
        return countryDto;
    }

}
