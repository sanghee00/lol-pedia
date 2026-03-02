package com.example.lolpedia.playerhistory.repository;

import com.example.lolpedia.playerhistory.entity.PlayerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerHistoryRepository extends JpaRepository<PlayerHistory, Long> {

    @Query("SELECT ph FROM PlayerHistory ph JOIN FETCH ph.team WHERE ph.player.playerId = :playerId")
    List<PlayerHistory> findByPlayer_PlayerId(@Param("playerId") Long playerId);
}
