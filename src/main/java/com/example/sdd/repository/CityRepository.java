package com.example.sdd.repository;

import com.example.sdd.entity.City;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CityRepository extends CrudRepository<City, Integer> {

    @Query("INSERT INTO city(name, country_id) VALUES (:name, :countryId) RETURNING id")
    Integer create(@Param("name") String name, @Param("countryId") Integer countryId);

    @Modifying
    @Query("UPDATE city SET name = :name, country_id = :countryId WHERE id = :id")
    void update(@Param("id") Integer id, @Param("name") String name, @Param("countryId") Integer countryId);

    @Query("SELECT * FROM city WHERE country_id = :countryId")
    List<City> findCityByCountryId(@Param("countryId") Integer countryId);

    @Query("SELECT * FROM city WHERE name = :name")
    List<City> findCityByName(@Param("name") String name);

}
