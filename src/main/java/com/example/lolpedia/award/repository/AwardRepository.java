package com.example.lolpedia.award.repository;

import com.example.lolpedia.award.entity.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {

    List<Award> findAwardByPlayer_PlayerId(Long playerId);

}
