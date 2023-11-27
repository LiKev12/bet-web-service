package com.bet.betwebservice.dao;

import com.bet.betwebservice.dto.PodCardDTO;
import com.bet.betwebservice.entity.PodEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface PodRepository extends JpaRepository<PodEntity, UUID> {
    Page<PodEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@RequestParam("name") String name, @RequestParam("description") String description, Pageable pageable);


    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.PodCardDTO(\n" +
            "        pod.id,\n" +
            "        pod.name, \n" +
            "        pod.description,\n" +
            "        pod.image,\n" +
            "        CAST(SUM(pod_user_pod_has_user.isMember) AS int)\n" +
            "    )\n" +
            "FROM PodEntity pod\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user on pod.id = pod_user_pod_has_user.idPod\n" +
            "GROUP BY pod.id\n" +
            "ORDER BY SUM(pod_user_pod_has_user.isMember) DESC, pod.name ASC")
    Page<PodCardDTO> getDiscoverPagePodCards(Pageable pageable);

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.PodCardDTO(\n" +
            "        pod.id,\n" +
            "        pod.name, \n" +
            "        pod.description,\n" +
            "        pod.image,\n" +
            "        CAST(SUM(pod_user_pod_has_user.isMember) AS int)\n" +
            "    )\n" +
            "FROM \n" +
            "\t(SELECT DISTINCT(pod.id) as distinctIdPod FROM PodEntity pod\n" +
            "\tLEFT JOIN TaskEntity task on task.idPod = pod.id\n" +
            "\tLEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "\tWHERE BIN_TO_UUID(stamp_task_stamp_has_task.idStamp) = :idStamp) AS joinDistinctIdPod\n" +
            "LEFT JOIN PodEntity pod on pod.id = distinctIdPod\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user on pod.id = pod_user_pod_has_user.idPod\n" +
            "GROUP BY pod.id\n" +
            "ORDER BY SUM(pod_user_pod_has_user.isMember) DESC, pod.name ASC")
    Page<PodCardDTO> getPodCardsAssociatedWithStamp(String idStamp, Pageable pageable);

}
