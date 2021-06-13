package com.example.sdd.repository;

import com.example.sdd.entity.Country;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CountryRepository extends ReactiveCrudRepository<Country, Integer> {

    @Query("INSERT INTO country(name) VALUES (:name) RETURNING id")
    Mono<Integer> create(@Param("name") String name);

    @Query("UPDATE country SET name = :name WHERE id = :id RETURNING id")
    Mono<Integer> update(@Param("id") Integer id, @Param("name") String name);

}
