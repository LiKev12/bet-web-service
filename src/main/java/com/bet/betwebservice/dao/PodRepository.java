package com.bet.betwebservice.dao;

import com.bet.betwebservice.entity.PodEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface PodRepository extends JpaRepository<PodEntity, UUID> {
    Page<PodEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@RequestParam("name") String name, @RequestParam("description") String description, Pageable pageable);
}
