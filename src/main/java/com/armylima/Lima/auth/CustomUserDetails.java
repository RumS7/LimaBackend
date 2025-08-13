package com.armylima.Lima.auth;

import com.armylima.Lima.dto.AccountStatus;
import com.armylima.Lima.dto.Rank;
import com.armylima.Lima.entities.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final UserInfo user;
    public CustomUserDetails(UserInfo user) { this.user = user; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRank() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRank().name()));
            if (user.getRank() == Rank.KING || user.getRank() == Rank.KNIGHT || user.getRank() == Rank.QUEEN || user.getRank() == Rank.BISHOP || user.getRank() == Rank.ROOK ) {
                authorities.add(new SimpleGrantedAuthority("ROLE_OFFICER"));
            } else if (user.getRank() == Rank.PAWN_SIPAHI) {
                authorities.add(new SimpleGrantedAuthority("ROLE_SOLDIER"));
            }
        }
        return authorities;
    }

    @Override public String getPassword() { return user.getPassword(); }
    @Override public String getUsername() { return user.getArmyId(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        if (user.getRank() == null) return false;
        boolean isOfficer = user.getRank() == Rank.KING || user.getRank() == Rank.KNIGHT || user.getRank() == Rank.QUEEN  || user.getRank() == Rank.ROOK;
        return isOfficer || user.getAccountStatus() == AccountStatus.ACTIVE;
    }
}