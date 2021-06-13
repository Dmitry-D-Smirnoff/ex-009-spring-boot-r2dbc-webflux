package com.example.sdd.repository;

import com.example.sdd.entity.City;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CityRepository extends ReactiveCrudRepository<City, Integer> {

    @Query("INSERT INTO city(name, country_id) VALUES (:name, :countryId) RETURNING id")
    Mono<Integer> create(@Param("name") String name, @Param("countryId") Integer countryId);

    @Query("UPDATE city SET name = :name, country_id = :countryId WHERE id = :id RETURNING id")
    Mono<Integer> update(@Param("id") Integer id, @Param("name") String name, @Param("countryId") Integer countryId);

    @Query("SELECT * FROM city WHERE country_id = :countryId")
    Flux<City> findCityByCountryId(@Param("countryId") Integer countryId);

    @Query("SELECT * FROM city WHERE name = :name")
    Flux<City> findCityByName(@Param("name") String name);

}
