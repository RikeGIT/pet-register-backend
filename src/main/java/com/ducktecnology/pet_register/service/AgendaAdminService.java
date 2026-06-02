package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.Perfil;
import com.ducktecnology.pet_register.domain.enums.StatusAgendamento;
import com.ducktecnology.pet_register.domain.model.Agendamento;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.agenda.AgendaEventoDTO;
import com.ducktecnology.pet_register.repository.AgendamentoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaAdminService {

    private final AgendamentoRepository agendamentoRepository;

    private Usuario usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    private void validarAdmin() {
        Usuario usuario = usuarioLogado();

        if (usuario.getPerfil() != Perfil.ADMIN && usuario.getPerfil() != Perfil.VETERINARIO) {
            throw new RuntimeException("Sem permissao para acessar a agenda administrativa");
        }
    }

    public List<AgendaEventoDTO> listarDoMes(Integer ano, Integer mes) {
        validarAdmin();

        LocalDate hoje = LocalDate.now();
        int anoAlvo = ano == null ? hoje.getYear() : ano;
        int mesAlvo = mes == null ? hoje.getMonthValue() : mes;

        LocalDateTime inicio = LocalDate.of(anoAlvo, mesAlvo, 1).atStartOfDay();
        LocalDateTime fim = inicio.plusMonths(1);

        return agendamentoRepository.listarAgendaDoPeriodo(StatusAgendamento.MARCADO, inicio, fim)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<AgendaEventoDTO> listarDoDia(LocalDate data) {
        validarAdmin();

        LocalDate dataAlvo = data == null ? LocalDate.now() : data;
        LocalDateTime inicio = dataAlvo.atStartOfDay();
        LocalDateTime fim = inicio.plusDays(1);

        return agendamentoRepository.listarAgendaDoDia(StatusAgendamento.MARCADO, inicio, fim)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public AgendaEventoDTO buscarDetalhe(Long id) {
        validarAdmin();

        Agendamento agendamento = agendamentoRepository.buscarDetalhadoPorId(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        return toDto(agendamento);
    }

    private AgendaEventoDTO toDto(Agendamento agendamento) {
        String nomeVeterinario = null;
        Long veterinarioId = null;
        Long solicitacaoId = null;
        String tipoSolicitacao = null;
        String descricaoSolicitacao = null;
        String contatoSolicitacao = null;
        LocalDate dataPreferencial = null;
        String nomeSolicitante = null;
        String cpfSolicitante = null;

        if (agendamento.getSolicitacao() != null) {
            solicitacaoId = agendamento.getSolicitacao().getId();
            tipoSolicitacao = agendamento.getSolicitacao().getTipo() != null
                    ? agendamento.getSolicitacao().getTipo().name()
                    : null;
            descricaoSolicitacao = agendamento.getSolicitacao().getDescricao();
            contatoSolicitacao = agendamento.getSolicitacao().getContato();
            dataPreferencial = agendamento.getSolicitacao().getDataPreferencial();

            if (agendamento.getSolicitacao().getUsuario() != null) {
                nomeSolicitante = agendamento.getSolicitacao().getUsuario().getNome();
                cpfSolicitante = agendamento.getSolicitacao().getUsuario().getCpf();
            }
        }

        if (agendamento.getHorario() != null && agendamento.getHorario().getVeterinario() != null) {
            veterinarioId = agendamento.getHorario().getVeterinario().getId();
            if (agendamento.getHorario().getVeterinario().getUsuario() != null) {
                nomeVeterinario = agendamento.getHorario().getVeterinario().getUsuario().getNome();
            }
        }

        return new AgendaEventoDTO(
                agendamento.getId(),
                agendamento.getAnimal() != null ? agendamento.getAnimal().getId() : null,
                agendamento.getAnimal() != null ? agendamento.getAnimal().getNome() : null,
                agendamento.getHorario() != null && agendamento.getHorario().getServico() != null
                        ? agendamento.getHorario().getServico().getId()
                        : null,
                agendamento.getHorario() != null && agendamento.getHorario().getServico() != null
                        ? agendamento.getHorario().getServico().getNome()
                        : null,
                solicitacaoId,
                tipoSolicitacao,
                descricaoSolicitacao,
                contatoSolicitacao,
                dataPreferencial,
                nomeSolicitante,
                cpfSolicitante,
                veterinarioId,
                nomeVeterinario,
                agendamento.getHorario() != null ? agendamento.getHorario().getDataHoraInicio() : null,
                agendamento.getHorario() != null ? agendamento.getHorario().getDataHoraFim() : null,
                agendamento.getStatus() != null ? agendamento.getStatus().name() : null
        );
    }
}
