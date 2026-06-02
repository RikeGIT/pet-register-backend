package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.usuario.AtualizarUsuarioPerfilDTO;
import com.ducktecnology.pet_register.dto.usuario.AtualizarUsuarioStatusDTO;
import com.ducktecnology.pet_register.dto.auth.OtpChallengeResponseDTO;
import com.ducktecnology.pet_register.dto.usuario.UsuarioAdminResponseDTO;
import com.ducktecnology.pet_register.dto.usuario.UsuarioRequestDTO;
import com.ducktecnology.pet_register.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<OtpChallengeResponseDTO> criar(@RequestBody @Valid UsuarioRequestDTO dto) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<UsuarioAdminResponseDTO>> listarAdmin() {
        return ResponseEntity.ok(service.listarAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id,
                                             @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}/perfil")
    public ResponseEntity<UsuarioAdminResponseDTO> atualizarPerfilAdmin(
            @PathVariable Long id,
            @RequestBody AtualizarUsuarioPerfilDTO dto) {
        return ResponseEntity.ok(service.atualizarPerfilAdmin(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioAdminResponseDTO> atualizarStatusAdmin(
            @PathVariable Long id,
            @RequestBody AtualizarUsuarioStatusDTO dto) {
        return ResponseEntity.ok(service.atualizarStatusAdmin(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
