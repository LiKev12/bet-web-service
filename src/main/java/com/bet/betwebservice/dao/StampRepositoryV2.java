package com.bet.betwebservice.dao;

import com.bet.betwebservice.dto.StampCardIndividualPropertiesDTO;
import com.bet.betwebservice.dto.StampCardSharedPropertiesDTO;
import com.bet.betwebservice.entity.PodEntity;
import com.bet.betwebservice.entity.StampEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StampRepositoryV2 extends JpaRepository<StampEntity, UUID> {

}
