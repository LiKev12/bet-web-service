package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    @Query("SELECT notification \n" +
            "FROM NotificationEntity notification \n" +
            "WHERE BIN_TO_UUID(notification.idUser) = :idUser")
    List<NotificationEntity> findByIdUser(
        @Param("idUser") String idUser
    );
}
