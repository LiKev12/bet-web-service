package com.bet.betwebservice.dao;

import com.bet.betwebservice.dto.UserBubbleDTO;
import com.bet.betwebservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryV2 extends JpaRepository<UserEntity, UUID> {

}
