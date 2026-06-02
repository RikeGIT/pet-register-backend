package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.dto.animal.CreateEspecieDTO;
import com.ducktecnology.pet_register.dto.animal.CreateRacaDTO;
import com.ducktecnology.pet_register.dto.animal.EspecieResponseDTO;
import com.ducktecnology.pet_register.dto.animal.UpdateEspecieDTO;
import com.ducktecnology.pet_register.dto.animal.UpdateRacaDTO;
import com.ducktecnology.pet_register.service.TaxonomiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaxonomiaController {

    private final TaxonomiaService service;

    @GetMapping("/api/public/taxonomias/especies")
    public ResponseEntity<List<EspecieResponseDTO>> listarEspeciesPublicas() {
        return ResponseEntity.ok(service.listarPublicas());
    }

    @GetMapping("/api/admin/taxonomias/especies")
    public ResponseEntity<List<EspecieResponseDTO>> listarEspeciesAdmin() {
        return ResponseEntity.ok(service.listarAdmin());
    }

    @PostMapping("/api/admin/taxonomias/especies")
    public ResponseEntity<EspecieResponseDTO> criarEspecie(@RequestBody CreateEspecieDTO dto) {
        return ResponseEntity.ok(service.criarEspecie(dto));
    }

    @PatchMapping("/api/admin/taxonomias/especies/{id}")
    public ResponseEntity<EspecieResponseDTO> atualizarEspecie(
            @PathVariable Long id,
            @RequestBody UpdateEspecieDTO dto
    ) {
        return ResponseEntity.ok(service.atualizarEspecie(id, dto));
    }

    @DeleteMapping("/api/admin/taxonomias/especies/{id}")
    public ResponseEntity<Void> deletarEspecie(@PathVariable Long id) {
        service.deletarEspecie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/admin/taxonomias/racas")
    public ResponseEntity<EspecieResponseDTO> criarRaca(@RequestBody CreateRacaDTO dto) {
        return ResponseEntity.ok(service.criarRaca(dto));
    }

    @PatchMapping("/api/admin/taxonomias/racas/{id}")
    public ResponseEntity<EspecieResponseDTO> atualizarRaca(
            @PathVariable Long id,
            @RequestBody UpdateRacaDTO dto
    ) {
        return ResponseEntity.ok(service.atualizarRaca(id, dto));
    }

    @DeleteMapping("/api/admin/taxonomias/racas/{id}")
    public ResponseEntity<EspecieResponseDTO> deletarRaca(@PathVariable Long id) {
        return ResponseEntity.ok(service.deletarRaca(id));
    }
}
