package de.lange.Stempeluhr.service;

import de.lange.Stempeluhr.Repository.ZeiterfassungRepository;
import de.lange.Stempeluhr.model.Zeiterfassung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ZeiterfassungService {
    @Autowired
    private ZeiterfassungRepository zeiterfassungRepository;

    public ZeiterfassungService(ZeiterfassungRepository zeiterfassungRepository) {
        this.zeiterfassungRepository = zeiterfassungRepository;
    }
    public List<Zeiterfassung> findByArbeitsDatum(LocalDate arbeitsDatum) {
        return zeiterfassungRepository.findByArbeitsDatum(arbeitsDatum);
    }

    public List<Zeiterfassung> getAllZeiterfassung() {
        return zeiterfassungRepository.findAll();
    }

    public Optional<Zeiterfassung> getZeiterfassungById(Integer id) {
        return zeiterfassungRepository.findById(id);
    }

    public void saveZeiterfassung(Zeiterfassung arbeitszeiten) {
        zeiterfassungRepository.save(arbeitszeiten);
    }
    private Zeiterfassung currentZeiterfassung;

    public Zeiterfassung handleArbeitszeit(String action) {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // Wenn die Aktion "startWork" ist, erstellen Sie einen neuen Zeiterfassung-Eintrag.
        if ("startWork".equals(action)) {
            if(currentZeiterfassung != null) throw new IllegalStateException("Es gibt bereits eine laufende Zeiterfassung!");

            currentZeiterfassung = new Zeiterfassung();
            currentZeiterfassung.setArbeitsDatum(currentTime.toLocalDate());
            currentZeiterfassung.setArbeitBeginn(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        } else {
            if(currentZeiterfassung == null) throw new IllegalStateException("Es gibt keinen laufenden Zeiterfassung-Eintrag!");

            switch (action) {
                case "endWork":
                    currentZeiterfassung.setArbeitEnde(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
                    calculateAndSetGesamtArbeitszeit(currentZeiterfassung);
                    saveZeiterfassung(currentZeiterfassung); // Speichert den abgeschlossenen Eintrag in der Datenbank
                    currentZeiterfassung = null; // Setzt den aktuellen Zeiterfassungseintrag zurück
                    break;

                case "startBreak":
                    currentZeiterfassung.setPauseBeginn(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
                    break;
                case "endBreak":
                    calculateAndSetPauseZeit(currentZeiterfassung);
                    break;
                default:
                    throw new IllegalArgumentException("Ungültige Aktion: " + action);
            }
        }

        saveZeiterfassung(currentZeiterfassung);
        return currentZeiterfassung;
    }

    private void endCurrentZeiterfassung() {
        if(currentZeiterfassung != null) {
            saveZeiterfassung(currentZeiterfassung);
            currentZeiterfassung = null;
        }
    }


    private void calculateAndSetPauseZeit(Zeiterfassung currentZeiterfassung) {
        LocalTime pauseEnde = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        currentZeiterfassung.setPauseEnde(pauseEnde);

        if (currentZeiterfassung.getPauseBeginn() != null) {
            LocalTime pauseBeginn = currentZeiterfassung.getPauseBeginn();
            Duration pauseDuration = Duration.between(pauseBeginn, pauseEnde);
            if (currentZeiterfassung.getGesamtPausenzeit() != null) {
                Duration gesamtPausenzeit = parseDuration(currentZeiterfassung.getGesamtPausenzeit());
                gesamtPausenzeit = gesamtPausenzeit.plus(pauseDuration);
                currentZeiterfassung.setGesamtPausenzeit(formatDuration(gesamtPausenzeit));
            } else {
                currentZeiterfassung.setGesamtPausenzeit(formatDuration(pauseDuration));
            }
        }
    }
    private void calculateAndSetGesamtArbeitszeit(Zeiterfassung arbeitszeiten) {
        LocalTime startWork = arbeitszeiten.getArbeitBeginn();
        LocalTime endWork = arbeitszeiten.getArbeitEnde();
        if (startWork != null && endWork != null) {
            Duration arbeitszeitOhnePause = Duration.between(startWork, endWork);
            if (arbeitszeiten.getGesamtPausenzeit() != null) {
                Duration gesamtPausenzeit = parseDuration(arbeitszeiten.getGesamtPausenzeit());
                Duration gesamtArbeitszeit = arbeitszeitOhnePause.minus(gesamtPausenzeit);
                arbeitszeiten.setGesamtArbeitszeit(formatDuration(gesamtArbeitszeit));
            }
        }
    }
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private Duration parseDuration(String duration) {
        String[] parts = duration.split(":");
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        long seconds = Long.parseLong(parts[2]);
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }




}