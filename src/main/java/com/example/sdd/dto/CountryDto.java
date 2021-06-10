package com.example.sdd.dto;

import com.example.sdd.entity.City;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CountryDto {

    private Integer id;
    private String name;
    private List<City> cities;

}
