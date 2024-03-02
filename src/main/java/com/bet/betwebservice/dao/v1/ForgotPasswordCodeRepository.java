package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.entity.ForgotPasswordCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ForgotPasswordCodeRepository extends JpaRepository<ForgotPasswordCodeEntity, UUID> {
    @Query("SELECT forgot_password_code \n" +
            "FROM ForgotPasswordCodeEntity forgot_password_code \n" +
            "WHERE BIN_TO_UUID(forgot_password_code.idUser) = :idUser ORDER BY forgot_password_code.timestampUnix DESC")
    List<ForgotPasswordCodeEntity> findByIdUser(
        @Param("idUser") String idUser
    );
}
