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

public interface StampRepository extends JpaRepository<StampEntity, UUID> {

    Optional<StampEntity> findById(UUID idStamp);

    @Query("SELECT stamp FROM StampEntity stamp WHERE LOWER(stamp.name) = LOWER(:stampName)")
    List<StampEntity> findByNameLowerCase(@Param("stampName") String stampName);

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.StampCardSharedPropertiesDTO(\n" +
            "        stamp.id,\n" +
            "        stamp.name, \n" +
            "        stamp.description,\n" +
            "        stamp.idImageKey,\n" +
            "        CAST(COUNT(DISTINCT(stamp_user_user_collect_stamp.idUser)) as int),\n" +
            "        MIN(CASE \n" +
            "            WHEN pod.isPublic THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isPublic\n" +
            "    )\n" +
            "FROM StampEntity stamp\n" +
            "LEFT JOIN StampUserUserCollectStampEntity stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.idStamp = stamp.id\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idStamp = stamp.id\n" +
            "LEFT JOIN TaskEntity task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "LEFT JOIN PodEntity pod on pod.id = task.idPod\n" +
            "WHERE task.isArchived = FALSE AND\n" +
            "LOWER(stamp.name) LIKE CONCAT('%',:filterByName,'%')\n" +
            "GROUP BY stamp.id")
    List<StampCardSharedPropertiesDTO> getStampCardsDiscover_SharedProperties(
            @Param("filterByName") String filterByName
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.StampCardIndividualPropertiesDTO(\n" +
            "        stamp.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(stamp_user_user_collect_stamp.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isCollect\n" +
            "    )\n" +
            "FROM StampEntity stamp\n" +
            "LEFT JOIN StampUserUserCollectStampEntity stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.idStamp = stamp.id\n" +
            "WHERE LOWER(stamp.name) LIKE CONCAT('%',:filterByName,'%')\n" +
            "GROUP BY stamp.id")
    List<StampCardIndividualPropertiesDTO> getStampCardsDiscover_IndividualProperties(
        @Param("idUser") String idUser,
        @Param("filterByName") String filterByName
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.StampCardSharedPropertiesDTO(\n" +
            "        stamp.id,\n" +
            "        stamp.name, \n" +
            "        stamp.description,\n" +
            "        stamp.idImageKey,\n" +
            "        CAST(COUNT(DISTINCT(stamp_user_user_collect_stamp.idUser)) as int),\n" +
            "        MIN(CASE \n" +
            "            WHEN pod.isPublic THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isPublic\n" +
            "    )\n" +
            "FROM StampEntity stamp\n" +
            "LEFT JOIN StampUserUserCollectStampEntity stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.idStamp = stamp.id\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idStamp = stamp.id\n" +
            "LEFT JOIN TaskEntity task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "LEFT JOIN PodEntity pod on pod.id = task.idPod\n" +
            "WHERE\n" +
            "task.isArchived = FALSE AND\n" +
            "BIN_TO_UUID(pod.id) = :idPod AND\n" +
            "(LOWER(stamp.name) LIKE CONCAT('%',:filterByName,'%'))\n" +
            "GROUP BY stamp.id")
    List<StampCardSharedPropertiesDTO> getStampCardsAssociatedWithPod_SharedProperties(
        @Param("idPod") String idPod, 
        @Param("filterByName") String filterByName
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.StampCardIndividualPropertiesDTO(\n" +
            "        stamp.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(stamp_user_user_collect_stamp.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isCollect\n" +
            "    )\n" +
            "FROM StampEntity stamp\n" +
            "LEFT JOIN StampUserUserCollectStampEntity stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.idStamp = stamp.id\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idStamp = stamp.id\n" +
            "LEFT JOIN TaskEntity task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "LEFT JOIN PodEntity pod on pod.id = task.idPod\n" +
            "WHERE\n" +
            "task.isArchived = FALSE AND\n" +
            "BIN_TO_UUID(pod.id) = :idPod AND\n" +
            "(LOWER(stamp.name) LIKE CONCAT('%',:filterByName,'%'))\n" +
            "GROUP BY stamp.id")
    List<StampCardIndividualPropertiesDTO> getStampCardsAssociatedWithPod_IndividualProperties(
        @Param("idPod") String idPod,
        @Param("idUser") String idUser,
        @Param("filterByName") String filterByName
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.StampCardSharedPropertiesDTO(\n" +
            "        stamp.id,\n" +
            "        stamp.name, \n" +
            "        stamp.description,\n" +
            "        stamp.idImageKey,\n" +
            "        CAST(COUNT(DISTINCT(stamp_user_user_collect_stamp.idUser)) as int),\n" +
            "        MIN(CASE \n" +
            "            WHEN pod.isPublic THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isPublic\n" +
            "    )\n" +
            "FROM StampEntity stamp\n" +
            "LEFT JOIN StampUserUserCollectStampEntity stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.idStamp = stamp.id\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idStamp = stamp.id\n" +
            "LEFT JOIN TaskEntity task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "LEFT JOIN PodEntity pod on pod.id = task.idPod\n" +
            "WHERE\n" +
            "task.isArchived = FALSE AND\n" +
            "(LOWER(stamp.name) LIKE CONCAT('%',:filterByName,'%'))\n" +
            "GROUP BY stamp.id")
    List<StampCardSharedPropertiesDTO> getStampCardsAssociatedWithUser_SharedProperties(
        @Param("filterByName") String filterByName
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.StampCardIndividualPropertiesDTO(\n" +
            "        stamp.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(stamp_user_user_collect_stamp.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isCollect\n" +
            "    )\n" +
            "FROM StampEntity stamp\n" +
            "LEFT JOIN StampUserUserCollectStampEntity stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.idStamp = stamp.id\n" +
            "WHERE\n" +
            "(LOWER(stamp.name) LIKE CONCAT('%',:filterByName,'%'))\n" +
            "GROUP BY stamp.id")
    List<StampCardIndividualPropertiesDTO> getStampCardsAssociatedWithUser_IndividualProperties(
        @Param("idUser") String idUser,
        @Param("filterByName") String filterByName
    );

   @Query("SELECT COUNT(DISTINCT(pod.id)) FROM StampEntity stamp\n" +
           "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idStamp = stamp.id\n" +
           "LEFT JOIN TaskEntity task on stamp_task_stamp_has_task.idTask = task.id\n" +
           "LEFT JOIN PodEntity pod on pod.id = task.idPod\n" +
           "WHERE BIN_TO_UUID(stamp.id) = :idStamp AND task.isArchived=FALSE")
   int getNumberOfPodsAssociatedWithStampTotal(@Param("idStamp") String idStamp);

   @Query("SELECT COUNT(DISTINCT(pod.id)) as numberOfPodsAssociatedWithStampUserIsMemberOf FROM StampEntity stamp\n" +
           "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idStamp = stamp.id\n" +
           "LEFT JOIN TaskEntity task on stamp_task_stamp_has_task.idTask = task.id\n" +
           "LEFT JOIN PodEntity pod on pod.id = task.idPod\n" +
           "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user on pod_user_pod_has_user.idPod = pod.id\n" +
           "WHERE BIN_TO_UUID(stamp.id) = :idStamp AND task.isArchived=FALSE AND\n" +
           "(BIN_TO_UUID(pod_user_pod_has_user.idUser) = :idUser AND pod_user_pod_has_user.isMember = TRUE)")
   int getNumberOfPodsAssociatedWithStampUserIsMemberOf(
           @Param("idStamp") String idStamp,
           @Param("idUser") String idUser
   );

   @Query("SELECT stamp_user_user_collect_stamp.idStamp FROM StampUserUserCollectStampEntity stamp_user_user_collect_stamp\n" +
            "WHERE BIN_TO_UUID(stamp_user_user_collect_stamp.idUser) = :idUser")
   List<UUID> getStampIdsUserHasCollected(@Param("idUser") String idUser);
}
