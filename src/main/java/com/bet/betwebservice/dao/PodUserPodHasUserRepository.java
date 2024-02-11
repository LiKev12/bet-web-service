package com.bet.betwebservice.dao;

import com.bet.betwebservice.entity.PodUserPodHasUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PodUserPodHasUserRepository extends JpaRepository<PodUserPodHasUserEntity, UUID> {
    @Query("SELECT pod_user_pod_has_user \n" +
            "FROM PodUserPodHasUserEntity pod_user_pod_has_user \n" +
            "WHERE BIN_TO_UUID(pod_user_pod_has_user.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(pod_user_pod_has_user.idPod) = :idPod")
    List<PodUserPodHasUserEntity> findByIdPodIdUser(
        @Param("idPod") String idPod, 
        @Param("idUser") String idUser
    );
}
