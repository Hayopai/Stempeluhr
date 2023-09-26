package de.lange.Stempeluhr.controller;

import de.lange.Stempeluhr.model.UsersModel;
import de.lange.Stempeluhr.model.Zeiterfassung;
import de.lange.Stempeluhr.service.ZeiterfassungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ZeiterfassungController {
    @Autowired
    private ZeiterfassungService zeiterfassungService;

    @GetMapping("/personal_page")
    public String getCurrentDateTime(Model model) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        model.addAttribute("currentDateTime", currentDateTime);
        List<Zeiterfassung> zeiterfassungList = zeiterfassungService.getAllZeiterfassung();
        model.addAttribute("zeiterfassungList", zeiterfassungList);
        return "personal_page";
    }
    

    @PostMapping("/personal_page")
    public String saveArbeitszeiten(@ModelAttribute Zeiterfassung zeiterfassung) {
        zeiterfassungService.saveZeiterfassung(zeiterfassung);
        return "redirect:/personal_page";
    }

    @PostMapping("/saveArbeitszeit")
    public String saveArbeitszeit(@RequestParam String action) {
        zeiterfassungService.handleArbeitszeit(action);
        return "redirect:/personal_page";
    }

}
