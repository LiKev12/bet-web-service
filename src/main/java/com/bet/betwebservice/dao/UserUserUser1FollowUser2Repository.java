package com.bet.betwebservice.dao;

import com.bet.betwebservice.entity.UserUserUser1FollowUser2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserUserUser1FollowUser2Repository extends JpaRepository<UserUserUser1FollowUser2Entity, UUID> {
    @Query("SELECT user_user_user_1_follow_user_2 \n" +
            "FROM UserUserUser1FollowUser2Entity user_user_user_1_follow_user_2 \n" +
            "WHERE BIN_TO_UUID(user_user_user_1_follow_user_2.idUser1) = :idUser1 AND\n" +
            "BIN_TO_UUID(user_user_user_1_follow_user_2.idUser2) = :idUser2")
    List<UserUserUser1FollowUser2Entity> findByIdUser1IdUser2(
        @Param("idUser1") String idUser1, 
        @Param("idUser2") String idUser2
    );
}
