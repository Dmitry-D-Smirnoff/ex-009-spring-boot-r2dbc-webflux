package com.example.sdd.repository;

import com.example.sdd.entity.Country;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CountryRepository extends CrudRepository<Country, Integer> {

    @Query("INSERT INTO country(name) VALUES (:name) RETURNING id")
    Integer create(@Param("name") String name);

    @Modifying
    @Query("UPDATE country SET name = :name WHERE id = :id")
    void update(@Param("id") Integer id, @Param("name") String name);


}
