// package com.bet.betwebservice.mappers;

// import com.bet.betwebservice.dto.SignUpDTO;
// import com.bet.betwebservice.dto.UserDTO;
// import com.bet.betwebservice.entity.UserEntity;
// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;

// public interface UserMapper {
//     UserDTO toUserDTO(UserEntity userEntity);

//     @Mapping(target="password", ignore=true)
//     UserEntity signUpToUserEntity(SignUpDTO signUpDTO);
// }