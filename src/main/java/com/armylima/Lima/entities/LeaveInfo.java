package com.armylima.Lima.entities;

import com.armylima.Lima.dto.Bty;
import com.armylima.Lima.dto.LeaveStatus;
import com.armylima.Lima.dto.Rank;
//import com.armylima.Lima.dto.Team;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


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
    private String location;

    @Transient
    public long getDuration(){

        if(fromDate==null || toDate==null) return 0;

        return ChronoUnit.DAYS.between(fromDate, toDate)+1;
    }

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

    @CreationTimestamp
    private LocalDate dateApplied;
    @Column(nullable = true, length=512)
    private String remarks;

    private boolean isLegacyRecord = false;

    private boolean approvedByBishop = false;
    private boolean approvedByKnight = false;
    private boolean approvedByKing = false;
    private boolean approvedByRook = false;
    private boolean approvedByQueen = false;


    @Column(nullable = true)
    private String rejectedById;
}