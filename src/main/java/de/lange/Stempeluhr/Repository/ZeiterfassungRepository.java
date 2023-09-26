package de.lange.Stempeluhr.Repository;

import de.lange.Stempeluhr.model.Zeiterfassung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ZeiterfassungRepository extends JpaRepository<Zeiterfassung, Integer> {

    List<Zeiterfassung> findByArbeitsDatum(LocalDate arbeitsDatum);

}
