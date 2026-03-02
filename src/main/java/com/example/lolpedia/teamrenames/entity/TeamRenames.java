package com.example.lolpedia.teamrenames.entity;

import com.example.lolpedia.team.entity.Team;
import com.example.lolpedia.teamrenames.enums.Verb;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_renames")
public class TeamRenames {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_rename_id")
    private Long teamRenameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "new_name")
    private String newName;

    @Column(name = "verb")
    @Enumerated(EnumType.STRING)
    private Verb verb;

    @Column(name = "date")
    private LocalDateTime date;

}
