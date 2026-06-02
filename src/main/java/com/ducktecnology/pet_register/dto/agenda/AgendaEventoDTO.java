package com.ducktecnology.pet_register.dto.agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AgendaEventoDTO(
        Long agendamentoId,
        Long animalId,
        String nomeAnimal,
        Long servicoId,
        String nomeServico,
        Long solicitacaoId,
        String tipoSolicitacao,
        String descricaoSolicitacao,
        String contatoSolicitacao,
        LocalDate dataPreferencial,
        String nomeSolicitante,
        String cpfSolicitante,
        Long veterinarioId,
        String nomeVeterinario,
        LocalDateTime inicio,
        LocalDateTime fim,
        String status
) {
}
