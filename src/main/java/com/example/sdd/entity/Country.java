package com.example.sdd.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Setter
@Getter
public class Country {

    @Id
    private Integer id;
    private String name;

}