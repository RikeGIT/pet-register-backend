package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.dto.agenda.AgendaEventoDTO;
import com.ducktecnology.pet_register.service.AgendaAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/agenda")
@RequiredArgsConstructor
public class AgendaAdminController {

    private final AgendaAdminService service;

    @GetMapping
    public ResponseEntity<List<AgendaEventoDTO>> listar(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes
    ) {
        return ResponseEntity.ok(service.listarDoMes(ano, mes));
    }

    @GetMapping("/dia")
    public ResponseEntity<List<AgendaEventoDTO>> listarDia(@RequestParam(required = false) LocalDate data) {
        return ResponseEntity.ok(service.listarDoDia(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaEventoDTO> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarDetalhe(id));
    }
}
