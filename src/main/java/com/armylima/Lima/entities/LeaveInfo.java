package com.armylima.Lima.entities;

import com.armylima.Lima.dto.Bty;
import com.armylima.Lima.dto.LeaveStatus;
import com.armylima.Lima.dto.Rank;
//import com.armylima.Lima.dto.Team;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserInfo user;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Rank pendingWithRank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Bty pendingWithBty;

    private boolean approvedByJCO = false;
    private boolean approvedByBC = false;
    private boolean approvedByCO = false;

    @Column(nullable = true)
    private String rejectedById;
}