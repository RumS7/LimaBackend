package com.armylima.Lima.auth;

import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;
    public CustomUserDetailsService(UserRepository repo){
        this.repo= repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        Optional<UserInfo> user = repo.findByArmyId(username);
        if (user.isPresent()) {
            return new CustomUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException("User not found with armyId: " + username);
        }

    }
}
