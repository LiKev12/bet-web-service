package com.bet.betwebservice.dao;

import com.bet.betwebservice.entity.StampUserUserCollectStampEntity;
import com.bet.betwebservice.entity.TaskUserTaskPinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StampUserUserCollectStampRepository extends JpaRepository<StampUserUserCollectStampEntity, UUID> {
    
    @Query("SELECT stamp_user_user_collect_stamp \n" +
        "FROM StampUserUserCollectStampEntity stamp_user_user_collect_stamp \n" +
        "WHERE BIN_TO_UUID(stamp_user_user_collect_stamp.idStamp) = :idStamp AND\n" +
        "BIN_TO_UUID(stamp_user_user_collect_stamp.idUser) = :idUser")
    List<StampUserUserCollectStampEntity> findByIdStampIdUser(
        @Param("idStamp") String idStamp, 
        @Param("idUser") String idUser
    );
}
