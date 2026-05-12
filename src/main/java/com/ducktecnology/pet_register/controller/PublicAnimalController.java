package com.ducktecnology.pet_register.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.ducktecnology.pet_register.dto.animal.PublicAnimalResponseDTO;
import com.ducktecnology.pet_register.service.PublicAnimalService;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicAnimalController {

    private final PublicAnimalService service;

    @GetMapping("/animals")
    public ResponseEntity<Page<PublicAnimalResponseDTO>> listarPublicos(
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) com.ducktecnology.pet_register.domain.enums.StatusAdocao status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.listarPublicos(especie, status, search, PageRequest.of(page, size)));
    }

    @GetMapping("/featured-animals")
    public ResponseEntity<List<PublicAnimalResponseDTO>> listarDestaques() {
        return ResponseEntity.ok(service.listarDestaques());
    }
}
