package de.lange.Stempeluhr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString


@Entity
public class Zeiterfassung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer z_id;

    private LocalTime arbeitBeginn;
    private LocalTime arbeitEnde;

    private LocalTime pauseBeginn;
    private LocalTime pauseEnde;

    private LocalDate arbeitsDatum;
    private String gesamtArbeitszeit;
    private String gesamtPausenzeit;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private UsersModel usersModel;

}
