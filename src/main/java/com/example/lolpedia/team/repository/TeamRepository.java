package com.example.lolpedia.team.repository;

import com.example.lolpedia.team.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// TODO: refactor
@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {

    @Query("SELECT t FROM Team t JOIN FETCH t.players WHERE t.teamName = :name")
    Optional<Team> findByTeamName(@Param("name") String teamName);

    @Query("SELECT t FROM Team t JOIN FETCH t.players p WHERE t.teamId = :id AND p.team.teamId = :id")
    Optional<Team> findByTeamId(@Param("id") Long teamId);

    @Query(value = "SELECT t.* FROM team t WHERE t.league = :league ORDER BY t.team_name ASC", nativeQuery = true)
    List<Team> findTopTeamsByLeague(@Param("league") String league, Pageable pageable);
    Page<Team> findAll(Specification<Team> spec, Pageable pageable);
}
