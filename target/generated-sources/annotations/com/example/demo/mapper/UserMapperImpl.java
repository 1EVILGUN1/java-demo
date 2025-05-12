package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-12T17:23:23+0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDto(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String dateOfBirth = null;

        id = user.getId();
        name = user.getName();
        dateOfBirth = user.getDateOfBirth();

        List<String> emails = user.getEmails().stream().map(com.example.demo.model.EmailData::getEmail).toList();
        List<String> phones = user.getPhones().stream().map(com.example.demo.model.Phone::getPhone).toList();

        UserDTO userDTO = new UserDTO( id, name, dateOfBirth, emails, phones );

        return userDTO;
    }

    @Override
    public List<UserDTO> toDtoList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>( users.size() );
        for ( User user : users ) {
            list.add( toDto( user ) );
        }

        return list;
    }
}
