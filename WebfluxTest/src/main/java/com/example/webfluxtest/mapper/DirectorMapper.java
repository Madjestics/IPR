package com.example.webfluxtest.mapper;

import com.example.webfluxtest.dto.DirectorDto;
import com.example.webfluxtest.entity.Director;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DirectorMapper {
    DirectorDto map(Director director);

    @InheritInverseConfiguration
    Director map(DirectorDto userDto);
}
