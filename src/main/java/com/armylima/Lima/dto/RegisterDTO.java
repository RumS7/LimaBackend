package com.armylima.Lima.dto;

public record RegisterDTO(
        String name,
        String email,
        String password,
        String armyId,
        Rank rank,
        String secretKey,
        Bty bty
) {}
