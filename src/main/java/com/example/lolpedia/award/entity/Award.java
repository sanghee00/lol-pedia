package com.example.lolpedia.award.entity;

import com.example.lolpedia.player.entity.Player;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "award")
public class Award {

    @Id
    @Column(name = "award_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long awardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "competition")
    private String competition;

    @Column(name = "title")
    private String title;

    @Column(name = "award_year")
    private String awardYear;

    @Column(name = "award_logo")
    private String awardLogo;

}
