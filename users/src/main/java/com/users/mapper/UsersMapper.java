package com.users.mapper;

import com.users.dto.UsersDTO;
import com.users.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    UsersDTO toUsersDTO(Users users);
    Users toUsersEntity(UsersDTO usersDTO);
}
