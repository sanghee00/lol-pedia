package com.example.lolpedia.playerhistory.entity;

import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.team.entity.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "player_history")
public class PlayerHistory {

    @Id
    @Column(name = "player_history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "start_contract_year")
    private String startContractYear;

    @Column(name = "end_contract_year")
    private String endContractYear;

    @Builder
    public PlayerHistory(Player player, Team team, String startContractYear, String endContractYear) {
        this.player = player;
        this.team = team;
        this.startContractYear = startContractYear;
        this.endContractYear = endContractYear;
    }
}
