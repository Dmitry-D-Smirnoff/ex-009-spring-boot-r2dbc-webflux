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
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        return countryRepository
                .findAll()
                .map(this::convert);
    }

    public Mono<CountryDto> getCountryById(Integer id) {
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

    public Mono<Mono<CountryDto>> createCountry(CountryCreateUpdateDto countryDto) {
        return countryRepository
                .create(countryDto.getName())
                .switchIfEmpty(Mono.error(new DataIntegrityViolationException("Country creation failed")))
                .handle((id, countrySynchronousSink) -> {
                    //TODO: Move city check to the method beginning!
                    // Here now - just to test transaction: rollback of new country created
                    for(City city : countryDto.getCities()){
                        cityRepository
                                .findCityByName(city.getName())
                                .doOnNext(existingCity ->
                                        countrySynchronousSink.error(new DuplicateKeyException("New country has existing cities"))
                                );
                        createCity(city.getName(), id)
                                .subscribe(savedCity -> {
                                    if(savedCity!=null){
                                        city.setId(savedCity.getId());
                                        city.setCountryId(id);
                                    }
                                    else{
                                        countrySynchronousSink.error(new DataAccessResourceFailureException("New city was not created"));
                                    }
                                });
                    }
                    // (Object) 'id' is THROWN here FURTHER along pipeline
                    countrySynchronousSink.next(id);
                })
                .map(id -> getCountryById((Integer) id))
                .switchIfEmpty(Mono.error(new DataIntegrityViolationException("Created country not found")))
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
                            //TODO: Вернуть, когда город будет удаляться
                            //.subscribe(cityRepository::delete);
                            .subscribe(city -> {
                                System.out.println("TRYING DELETE: " + city.getName());
                                //TODO: Почему найденный город не удаляется из БД сразу, только после следующей операции???
                                cityRepository
                                        .delete(city)
                                        //TODO: Почему мы никогда не попадаем внутрь subscribe???
                                        .subscribe(Void -> System.out.println("DELETED: " + city.getName()));
                            });
                    countrySynchronousSink.next(country);
                })
                //TODO: Тут ВНАЧАЛЕ нужно добавление в БД новых городов ДО апдейта Страны
                .handle((country, countrySynchronousSink) -> {
                    System.out.println("TRYING UPDATE TO: " + countryDto.getName());
                    countryRepository
                            //TODO: Почему страна не обновляется в БД сразу, только после следующей операции???
                            .update(id, countryDto.getName())
                            //TODO: Почему мы никогда не попадаем внутрь subscribe???
                            .subscribe(Void -> System.out.println("UPDATED1 TO: " + countryDto.getName()));
                    countryRepository
                            .update(id, countryDto.getName())
                            //TODO: Почему мы никогда не попадаем внутрь subscribe???
                            .subscribe(Void -> System.out.println("UPDATED2 TO: " + countryDto.getName()));
                    countrySynchronousSink.next(country);
                })
                .map(country -> convert((Country) country));


        /*
        //TODO: Перенести наверх внесение НОВЫХ городов, когда город будет удаляться
        for(City city : countryDtoCities){
            List<City> existingCities = cityRepository.findCityByName(city.getName());
            if(CollectionUtils.isEmpty(existingCities)){
                cityRepository.create(city.getName(), id);
            }
        }
         */
    }

    public Mono<Void> deleteCountry(Integer id) {
        return countryRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new DataRetrievalFailureException("Country Not Found")))
                .handle((country, synchronousSink) ->
                        cityRepository
                                .findCityByCountryId(id)
                                .doOnNext(cityRepository::delete)
                                .doOnComplete(() -> countryRepository.delete(country))
                );
    }

    public CountryDto convert(Country country){
        CountryDto countryDto = mapper.map(country, CountryDto.class);
        countryDto.setCities(cityRepository.findCityByCountryId(country.getId()));
        return countryDto;
    }

}
