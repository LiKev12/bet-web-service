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

public interface PodRepository extends JpaRepository<PodEntity, UUID> {

    Optional<PodEntity> findById(UUID idPod);

    @Query("SELECT pod FROM PodEntity pod WHERE LOWER(pod.name) = LOWER(:podName)")
    List<PodEntity> findByNameLowerCase(@Param("podName") String podName);

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.PodCardSharedPropertiesDTO(\n" +
            "        pod.id,\n" +
            "        pod.name, \n" +
            "        pod.description,\n" +
            "        pod.idImageKey,\n" +
            "        pod.isPublic,\n" +
            "        CAST(COUNT(DISTINCT(CASE\n" +
            "            WHEN pod_user_pod_has_user.isMember = TRUE THEN pod_user_pod_has_user.idUser\n" +
            "            ELSE NULL\n" +
            "        END)) as int) as numberOfMembers\n" +
            "    )\n" +
            "FROM PodEntity pod\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user ON pod_user_pod_has_user.idPod = pod.id\n" +
            "WHERE (LOWER(pod.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(pod.description) LIKE CONCAT('%',:filterNameOrDescription,'%')) AND\n" +
            "(pod.isPublic = :filterIsPublic OR pod.isPublic != :filterIsNotPublic)\n" +
            "GROUP BY pod.id")
    List<PodCardSharedPropertiesDTO> getPodCardsDiscover_SharedProperties(
            @Param("filterNameOrDescription") String filterNameOrDescription,
            @Param("filterIsPublic") boolean filterIsPublic,
            @Param("filterIsNotPublic") boolean filterIsNotPublic
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.PodCardIndividualPropertiesDTO(\n" +
            "        pod.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(pod_user_pod_has_user.idUser) = :idUser AND pod_user_pod_has_user.isMember = TRUE THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isMember,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(pod_user_pod_has_user.idUser) = :idUser AND pod_user_pod_has_user.isModerator = TRUE THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isModerator\n" +
            "    )\n" +
            "FROM PodEntity pod\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user on pod_user_pod_has_user.idPod = pod.id\n" +
            "WHERE (LOWER(pod.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(pod.description) LIKE CONCAT('%',:filterNameOrDescription,'%')) AND\n" +
            "(pod.isPublic = :filterIsPublic OR pod.isPublic != :filterIsNotPublic)\n" +
            "GROUP BY pod.id")
    List<PodCardIndividualPropertiesDTO> getPodCardsDiscover_IndividualProperties(
            @Param("idUser") String idUser,
            @Param("filterNameOrDescription") String filterNameOrDescription,
            @Param("filterIsPublic") boolean filterIsPublic,
            @Param("filterIsNotPublic") boolean filterIsNotPublic
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.PodCardSharedPropertiesDTO(\n" +
            "        pod.id,\n" +
            "        pod.name, \n" +
            "        pod.description,\n" +
            "        pod.idImageKey,\n" +
            "        pod.isPublic,\n" +
            "        CAST(COUNT(DISTINCT(CASE\n" +
            "            WHEN pod_user_pod_has_user.isMember = TRUE THEN pod_user_pod_has_user.idUser\n" +
            "            ELSE NULL\n" +
            "        END)) as int) as numberOfMembers\n" +
            "    )\n" +
            "FROM PodEntity pod\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user ON pod_user_pod_has_user.idPod = pod.id\n" +
            "LEFT JOIN TaskEntity task on task.idPod = pod.id\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "LEFT JOIN StampEntity stamp on stamp.id = stamp_task_stamp_has_task.idStamp\n" +
            "WHERE BIN_TO_UUID(stamp.id) = :idStamp AND task.isArchived=FALSE AND\n" +
            "(LOWER(pod.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(pod.description) LIKE CONCAT('%',:filterNameOrDescription,'%')) AND\n" +
            "(pod.isPublic = :filterIsPublic OR pod.isPublic != :filterIsNotPublic)\n" +
            "GROUP BY pod.id")
    List<PodCardSharedPropertiesDTO> getPodCardsAssociatedWithStamp_SharedProperties(
        @Param("idStamp") String idStamp,
        @Param("filterNameOrDescription") String filterNameOrDescription,
        @Param("filterIsPublic") boolean filterIsPublic,
        @Param("filterIsNotPublic") boolean filterIsNotPublic
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.PodCardIndividualPropertiesDTO(\n" +
            "        pod.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(pod_user_pod_has_user.idUser) = :idUser AND pod_user_pod_has_user.isMember = TRUE THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isMember,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(pod_user_pod_has_user.idUser) = :idUser AND pod_user_pod_has_user.isModerator = TRUE THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isModerator\n" +
            "    )\n" +
            "FROM PodEntity pod\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user on pod_user_pod_has_user.idPod = pod.id\n" +
            "LEFT JOIN TaskEntity task on task.idPod = pod.id\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "LEFT JOIN StampEntity stamp on stamp.id = stamp_task_stamp_has_task.idStamp\n" +
            "WHERE BIN_TO_UUID(stamp.id) = :idStamp AND task.isArchived=FALSE AND\n" +
            "(LOWER(pod.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(pod.description) LIKE CONCAT('%',:filterNameOrDescription,'%')) AND\n" +
            "(pod.isPublic = :filterIsPublic OR pod.isPublic != :filterIsNotPublic)\n" +
            "GROUP BY pod.id")
    List<PodCardIndividualPropertiesDTO> getPodCardsAssociatedWithStamp_IndividualProperties(
        @Param("idStamp") String idStamp,
        @Param("idUser") String idUser,
        @Param("filterNameOrDescription") String filterNameOrDescription,
        @Param("filterIsPublic") boolean filterIsPublic,
        @Param("filterIsNotPublic") boolean filterIsNotPublic
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.PodCardSharedPropertiesDTO(\n" +
            "        pod.id,\n" +
            "        pod.name, \n" +
            "        pod.description,\n" +
            "        pod.idImageKey,\n" +
            "        pod.isPublic,\n" +
            "        CAST(COUNT(DISTINCT(CASE\n" +
            "            WHEN pod_user_pod_has_user.isMember = TRUE THEN pod_user_pod_has_user.idUser\n" +
            "            ELSE NULL\n" +
            "        END)) as int) as numberOfMembers\n" +
            "    )\n" +
            "FROM PodEntity pod\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user ON pod_user_pod_has_user.idPod = pod.id\n" +
            "WHERE (LOWER(pod.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(pod.description) LIKE CONCAT('%',:filterNameOrDescription,'%')) AND\n" +
            "(pod.isPublic = :filterIsPublic OR pod.isPublic != :filterIsNotPublic)\n" +
            "GROUP BY pod.id\n")
    List<PodCardSharedPropertiesDTO> getPodCardsAssociatedWithUser_SharedProperties(
        @Param("filterNameOrDescription") String filterNameOrDescription,
        @Param("filterIsPublic") boolean filterIsPublic,
        @Param("filterIsNotPublic") boolean filterIsNotPublic
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.PodCardIndividualPropertiesDTO(\n" +
            "        pod.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(pod_user_pod_has_user.idUser) = :idUser AND pod_user_pod_has_user.isMember = TRUE THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isMember,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(pod_user_pod_has_user.idUser) = :idUser AND pod_user_pod_has_user.isModerator = TRUE THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 as isModerator\n" +
            "    )\n" +
            "FROM PodEntity pod\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user on pod_user_pod_has_user.idPod = pod.id\n" +
            "WHERE (LOWER(pod.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(pod.description) LIKE CONCAT('%',:filterNameOrDescription,'%')) AND\n" +
            "(pod.isPublic = :filterIsPublic OR pod.isPublic != :filterIsNotPublic)\n" +
            "GROUP BY pod.id")
    List<PodCardIndividualPropertiesDTO> getPodCardsAssociatedWithUser_IndividualProperties(
        @Param("idUser") String idUser,
        @Param("filterNameOrDescription") String filterNameOrDescription,
        @Param("filterIsPublic") boolean filterIsPublic,
        @Param("filterIsNotPublic") boolean filterIsNotPublic
    );

    @Query("SELECT pod_user_pod_has_user.idPod FROM PodUserPodHasUserEntity pod_user_pod_has_user\n" +
            "WHERE BIN_TO_UUID(pod_user_pod_has_user.idUser) = :idUser AND pod_user_pod_has_user.isMember = TRUE")
    List<UUID> getPodIdsUserIsMemberOf(@Param("idUser") String idUser);

    @Query("SELECT pod_user_pod_has_user.idUser FROM PodUserPodHasUserEntity pod_user_pod_has_user\n" +
            "WHERE BIN_TO_UUID(pod_user_pod_has_user.idPod) = :idPod AND pod_user_pod_has_user.isJoinPodInviteSent = TRUE AND pod_user_pod_has_user.isJoinPodInviteAccepted = FALSE")
    List<UUID> getUserIdsJoinPodInviteSentNotYetAccepted(@Param("idPod") String idPod);

    @Query("SELECT pod_user_pod_has_user.idUser FROM PodUserPodHasUserEntity pod_user_pod_has_user\n" +
    "WHERE BIN_TO_UUID(pod_user_pod_has_user.idPod) = :idPod AND pod_user_pod_has_user.isBecomePodModeratorRequestSent = TRUE AND pod_user_pod_has_user.isBecomePodModeratorRequestApproved = FALSE")
    List<UUID> getUserIdsBecomePodModeratorRequestSentNotYetApproved(@Param("idPod") String idPod);

    @Query("SELECT pod_user_pod_has_user.idUser FROM PodUserPodHasUserEntity pod_user_pod_has_user\n" +
    "WHERE BIN_TO_UUID(pod_user_pod_has_user.idPod) = :idPod AND pod_user_pod_has_user.isBecomePodModeratorRequestSent = TRUE")
    List<UUID> getUserIdsBecomePodModeratorRequestSent(@Param("idPod") String idPod);

    @Query("SELECT pod.id FROM PodEntity pod\n" +
            "WHERE pod.isPublic = TRUE")
    List<UUID> getPodIdsPublicPod();
}
