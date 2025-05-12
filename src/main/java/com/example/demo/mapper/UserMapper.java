package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "emails", expression = "java(user.getEmails().stream().map(com.example.demo.model.EmailData::getEmail).toList())")
    @Mapping(target = "phones", expression = "java(user.getPhones().stream().map(com.example.demo.model.Phone::getPhone).toList())")
    UserDTO toDto(User user);

    List<UserDTO> toDtoList(List<User> users);
}