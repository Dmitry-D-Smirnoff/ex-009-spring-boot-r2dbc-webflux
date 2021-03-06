package com.example.sdd.dto;

import com.example.sdd.entity.City;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;

@Setter
@Getter
public class CountryDto {

    private Integer id;
    private String name;
    private Flux<City> cities;

}
