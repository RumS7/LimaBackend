package com.armylima.Lima.entities;

import com.armylima.Lima.dto.AccountStatus;
import com.armylima.Lima.dto.Bty;
import com.armylima.Lima.dto.Rank;
//import com.armylima.Lima.dto.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class UserInfo {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) private Long id;
    private String name;
    private String email;
    private String password;
    @Column(unique = true) private String armyId;
    @Enumerated(EnumType.STRING) private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;
    @Enumerated(EnumType.STRING) private Rank rank;
    @Enumerated(EnumType.STRING) private Bty bty;
}