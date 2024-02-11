package com.bet.betwebservice.dao;

import com.bet.betwebservice.dto.UserBubbleDTO;
import com.bet.betwebservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT user FROM UserEntity user WHERE LOWER(user.username) = LOWER(:userUsername)")
    List<UserEntity> findByUsernameLowerCase(@Param("userUsername") String userUsername);

    @Query("SELECT timestampUnix \n" +
            "FROM UserEntity user \n" +
            "WHERE BIN_TO_UUID(user.id) = :idUser")
    Integer getTimestampUserCreateAccount(String idUser);

    @Query("SELECT MIN(task_user_task_complete.timestampUnix) \n" +
            "FROM TaskUserTaskCompleteEntity task_user_task_complete\n" +
            "LEFT JOIN TaskEntity task on task.id = task_user_task_complete.idTask\n" +
            "LEFT JOIN PodEntity pod on pod.id = task.idPod\n" +
            "WHERE BIN_TO_UUID(pod.id) = :idPod AND BIN_TO_UUID(task_user_task_complete.idUser) = :idUser AND task.isArchived=FALSE")
    Integer getTimestampUserFirstCompleteAnyTaskAssociatedWithPod(
        @Param("idPod") String idPod, 
        @Param("idUser") String idUser
    );

    @Query("SELECT MIN(task_user_task_complete.timestampUnix) \n" +
            "FROM TaskUserTaskCompleteEntity task_user_task_complete\n" +
            "LEFT JOIN TaskEntity task on task.id = task_user_task_complete.idTask\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idTask = task.id\n" +
        "WHERE BIN_TO_UUID(stamp_task_stamp_has_task.idStamp) = :idStamp AND BIN_TO_UUID(task_user_task_complete.idUser) = :idUser AND task.isArchived=FALSE")
    Integer getTimestampUserFirstCompleteAnyTaskAssociatedWithStamp(
        @Param("idStamp") String idStamp, 
        @Param("idUser") String idUser
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.UserBubbleDTO(\n" +
            "        user.id,\n" +
            "        user.name, \n" +
            "        user.username, \n" +
            "        user.idImageKey,\n" +
            "        MAX(task_user_task_complete.timestampUnix) as timestampToSortBy\n" +
            "    )\n" +
            "FROM UserEntity user\n" +
            "LEFT JOIN TaskUserTaskCompleteEntity task_user_task_complete ON task_user_task_complete.idUser = user.id\n" +
            "LEFT JOIN TaskEntity task ON task.id = task_user_task_complete.idTask\n" +
            "WHERE BIN_TO_UUID(task.id) = :idTask AND task.isArchived=FALSE\n" +
            "GROUP BY user.id")
    List<UserBubbleDTO> getUserBubblesTaskComplete(@Param("idTask") String idTask);

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.UserBubbleDTO(\n" +
            "        user.id,\n" +
            "        user.name, \n" +
            "        user.username, \n" +
            "        user.idImageKey,\n" +
            "        MAX(pod_user_pod_has_user.timestampBecomeMember) as timestampToSortBy\n" +
            "    )\n" +
            "FROM UserEntity user\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user ON pod_user_pod_has_user.idUser = user.id\n" +
            "LEFT JOIN PodEntity pod ON pod.id = pod_user_pod_has_user.idPod\n" +
            "WHERE BIN_TO_UUID(pod.id) = :idPod AND pod_user_pod_has_user.isMember = TRUE\n" +
            "GROUP BY user.id")
    List<UserBubbleDTO> getUserBubblesPodMember(@Param("idPod") String idPod);

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.UserBubbleDTO(\n" +
            "        user.id,\n" +
            "        user.name, \n" +
            "        user.username, \n" +
            "        user.idImageKey,\n" +
            "        MAX(pod_user_pod_has_user.timestampBecomeModerator) as timestampToSortBy\n" +
            "    )\n" +
            "FROM UserEntity user\n" +
            "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user ON pod_user_pod_has_user.idUser = user.id\n" +
            "LEFT JOIN PodEntity pod ON pod.id = pod_user_pod_has_user.idPod\n" +
            "WHERE BIN_TO_UUID(pod.id) = :idPod AND pod_user_pod_has_user.isModerator = TRUE\n" +
            "GROUP BY user.id")
    List<UserBubbleDTO> getUserBubblesPodModerator(@Param("idPod") String idPod);

    @Query("SELECT \n" +
    "    new com.bet.betwebservice.dto.UserBubbleDTO(\n" +
    "        user.id,\n" +
    "        user.name, \n" +
    "        user.username, \n" +
    "        user.idImageKey,\n" +
    "        MAX(pod_user_pod_has_user.timestampBecomePodModeratorRequestSent) as timestampToSortBy\n" +
    "    )\n" +
    "FROM UserEntity user\n" +
    "LEFT JOIN PodUserPodHasUserEntity pod_user_pod_has_user ON pod_user_pod_has_user.idUser = user.id\n" +
    "LEFT JOIN PodEntity pod ON pod.id = pod_user_pod_has_user.idPod\n" +
    "WHERE BIN_TO_UUID(pod.id) = :idPod AND pod_user_pod_has_user.isBecomePodModeratorRequestSent = TRUE AND pod_user_pod_has_user.isModerator = FALSE\n" +
    "GROUP BY user.id")
    List<UserBubbleDTO> getUserBubblesBecomePodModeratorRequestSentNotYetApproved(@Param("idPod") String idPod);

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.UserBubbleDTO(\n" +
            "        user.id,\n" +
            "        user.name, \n" +
            "        user.username, \n" +
            "        user.idImageKey,\n" +
            "        MAX(stamp_user_user_collect_stamp.timestampUnix) as timestampToSortBy\n" +
            "    )\n" +
            "FROM UserEntity user\n" +
            "LEFT JOIN StampUserUserCollectStampEntity stamp_user_user_collect_stamp ON stamp_user_user_collect_stamp.idUser = user.id\n" +
            "WHERE BIN_TO_UUID(stamp_user_user_collect_stamp.idStamp) = :idStamp\n" +
            "GROUP BY user.id")
    List<UserBubbleDTO> getUserBubblesStampCollect(@Param("idStamp") String idStamp);

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.UserBubbleDTO(\n" +
            "        user.id,\n" +
            "        user.name, \n" +
            "        user.username, \n" +
            "        user.idImageKey,\n" +
            "        MAX(user_user_user_1_follow_user_2.timestampOfFollowing) as timestampToSortBy\n" +
            "    )\n" +
            "FROM UserEntity user\n" +
            "LEFT JOIN UserUserUser1FollowUser2Entity user_user_user_1_follow_user_2 ON user_user_user_1_follow_user_2.idUser2 = user.id\n" +
            "WHERE BIN_TO_UUID(user_user_user_1_follow_user_2.idUser1) = :idUser AND user_user_user_1_follow_user_2.isFollowing = TRUE\n" +
            "GROUP BY user.id")
    List<UserBubbleDTO> getUserBubblesFollowing(@Param("idUser") String idUser);

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.UserBubbleDTO(\n" +
            "        user.id,\n" +
            "        user.name, \n" +
            "        user.username, \n" +
            "        user.idImageKey,\n" +
            "        MAX(user_user_user_1_follow_user_2.timestampOfFollowing) as timestampToSortBy\n" +
            "    )\n" +
            "FROM UserEntity user\n" +
            "LEFT JOIN UserUserUser1FollowUser2Entity user_user_user_1_follow_user_2 ON user_user_user_1_follow_user_2.idUser1 = user.id\n" +
            "WHERE BIN_TO_UUID(user_user_user_1_follow_user_2.idUser2) = :idUser AND user_user_user_1_follow_user_2.isFollowing = TRUE\n" +
            "GROUP BY user.id")
    List<UserBubbleDTO> getUserBubblesFollower(@Param("idUser") String idUser);
    
    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.UserBubbleDTO(\n" +
            "        user.id,\n" +
            "        user.name, \n" +
            "        user.username, \n" +
            "        user.idImageKey,\n" +
            "        MAX(user_user_user_1_follow_user_2.timestampRequestSent) as timestampToSortBy\n" +
            "    )\n" +
            "FROM UserEntity user\n" +
            "LEFT JOIN UserUserUser1FollowUser2Entity user_user_user_1_follow_user_2 ON user_user_user_1_follow_user_2.idUser1 = user.id\n" +
            "WHERE BIN_TO_UUID(user_user_user_1_follow_user_2.idUser2) = :idUser AND user_user_user_1_follow_user_2.isRequestSent = TRUE AND user_user_user_1_follow_user_2.isFollowing = FALSE\n" +
            "GROUP BY user.id")
    List<UserBubbleDTO> getUserBubblesFollowUserRequestSentNotYetAccepted(@Param("idUser") String idUser);



    @Query("SELECT user_user_user_1_follow_user_2.idUser2 FROM UserUserUser1FollowUser2Entity user_user_user_1_follow_user_2\n" +
            "WHERE BIN_TO_UUID(user_user_user_1_follow_user_2.idUser1) = :idUser AND user_user_user_1_follow_user_2.isFollowing = TRUE")
    List<UUID> getUserIdsFollowedByGivenUser(@Param("idUser") String idUser);

    @Query("SELECT user_user_user_1_follow_user_2.idUser2 FROM UserUserUser1FollowUser2Entity user_user_user_1_follow_user_2\n" +
            "WHERE BIN_TO_UUID(user_user_user_1_follow_user_2.idUser1) = :idUser AND user_user_user_1_follow_user_2.isRequestSent = TRUE AND user_user_user_1_follow_user_2.isFollowing = FALSE")
    List<UUID> getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(@Param("idUser") String idUser);

    @Query("SELECT user_user_user_1_follow_user_2.idUser1 FROM UserUserUser1FollowUser2Entity user_user_user_1_follow_user_2\n" +
            "WHERE BIN_TO_UUID(user_user_user_1_follow_user_2.idUser2) = :idUser AND user_user_user_1_follow_user_2.isRequestSent = TRUE AND user_user_user_1_follow_user_2.isFollowing = FALSE")
    List<UUID> getUserIdsFollowRequestSentToGivenUser_NotYetAccepted(@Param("idUser") String idUser);
}
