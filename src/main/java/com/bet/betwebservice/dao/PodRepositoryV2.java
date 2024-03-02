package com.bet.betwebservice.dao;

import com.bet.betwebservice.dto.PodCardIndividualPropertiesDTO;
import com.bet.betwebservice.dto.PodCardSharedPropertiesDTO;
import com.bet.betwebservice.entity.PodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PodRepositoryV2 extends JpaRepository<PodEntity, UUID> {

}
