package com.mashi.omnicanal.analitica.controller;

import com.mashi.omnicanal.analitica.dto.ResumenAnaliticaDTO;
import com.mashi.omnicanal.analitica.service.AnaliticaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analitica")
public class AnaliticaController {

    private final AnaliticaService analiticaService;

    public AnaliticaController(AnaliticaService analiticaService) {
        this.analiticaService = analiticaService;
    }

    @GetMapping("/resumen")
    public ResumenAnaliticaDTO resumen(@RequestParam(defaultValue = "30") int dias) {
        return analiticaService.resumen(dias);
    }
}
