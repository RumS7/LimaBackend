package com.armylima.Lima.repositories;


import com.armylima.Lima.entities.HealthReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface HealthReportRepository extends JpaRepository<HealthReport, Long> {

    // Spring Data JPA will automatically create the query for this method
    List<HealthReport> findByArmyId(String armyId);

    Optional<HealthReport> findByArmyIdAndReportDate(String armyId, LocalDate today);
}