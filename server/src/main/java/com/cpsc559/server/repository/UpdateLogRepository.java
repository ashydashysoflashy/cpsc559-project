package com.cpsc559.server.repository;

import com.cpsc559.server.model.UpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UpdateLogRepository extends JpaRepository<UpdateLog, Integer> {
    List<UpdateLog> findByTimestampGreaterThanOrderByTimestampAsc(int ts);

    @Query("select max(u.timestamp) from UpdateLog u")
    Integer findMaxTimestamp();
}
