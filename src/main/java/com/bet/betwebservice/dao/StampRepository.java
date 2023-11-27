package com.bet.betwebservice.dao;

import com.bet.betwebservice.dto.StampCardDTO;
import com.bet.betwebservice.entity.StampEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface StampRepository extends JpaRepository<StampEntity, UUID> {
    Page<StampEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@RequestParam("name") String name, @RequestParam("description") String description, Pageable pageable);


    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.StampCardDTO(\n" +
            "        stamp.id,\n" +
            "        stamp.name, \n" +
            "        stamp.description,\n" +
            "        stamp.image,\n" +
            "        CAST(COUNT(*) AS int)\n" +
            "    )\n" +
            "FROM StampEntity stamp\n" +
            "LEFT JOIN StampUserUserCollectStampEntity stamp_user_user_collect_stamp on stamp.id = stamp_user_user_collect_stamp.idStamp\n" +
            "GROUP BY stamp.id\n" +
            "ORDER BY COUNT(*) DESC, stamp.name ASC")
    Page<StampCardDTO> getDiscoverPageStampCards(Pageable pageable);

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.StampCardDTO(\n" +
            "        stamp.id,\n" +
            "        stamp.name, \n" +
            "        stamp.description,\n" +
            "        stamp.image,\n" +
            "        CAST(COUNT(stamp_user_user_collect_stamp.id) AS int)\n" +
            "    )\n" +
            "FROM\n" +
            "    (SELECT DISTINCT(stamp.id) as distinctIdStamp FROM StampEntity stamp\n" +
            "    LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idStamp = stamp.id\n" +
            "    LEFT JOIN TaskEntity task on task.id = stamp_task_stamp_has_task.idTask\n" +
            "    LEFT JOIN PodEntity pod on pod.id = task.idPod\n" +
            "    WHERE BIN_TO_UUID(pod.id) = idPod) AS joinDistinctIdStamp\n" +
            "LEFT JOIN StampEntity stamp on stamp.id = distinctIdStamp\n" +
            "LEFT JOIN StampUserUserCollectStampEntity stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.idStamp = stamp.id\n" +
            "GROUP BY stamp.id\n" +
            "ORDER BY COUNT(stamp_user_user_collect_stamp.id) DESC, stamp.name ASC")
    Page<StampCardDTO> getStampCardsAssociatedWithPod(String idPod, Pageable pageable);
}
