package com.example.sdd.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Setter
@Getter
public class City {

    @Id
    private Integer id;
    private String name;
    private Integer countryId;

}
