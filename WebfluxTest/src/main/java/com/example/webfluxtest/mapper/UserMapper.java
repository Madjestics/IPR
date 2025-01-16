package com.example.webfluxtest.mapper;

import com.example.webfluxtest.dto.UserDto;
import com.example.webfluxtest.entity.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDto map(User user);

    @InheritInverseConfiguration
    User map(UserDto userDto);
}
