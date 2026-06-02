package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.StatusSolicitacao;
import com.ducktecnology.pet_register.domain.enums.StatusAgendamento;
import com.ducktecnology.pet_register.domain.enums.StatusHorario;
import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.domain.model.Agendamento;
import com.ducktecnology.pet_register.domain.model.HorarioAtendimento;
import com.ducktecnology.pet_register.domain.model.Veterinario;
import com.ducktecnology.pet_register.domain.model.Solicitacao;
import com.ducktecnology.pet_register.domain.model.Servico;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.domain.enums.Perfil;
import com.ducktecnology.pet_register.dto.solicitacao.SolicitacaoRequestDTO;
import com.ducktecnology.pet_register.dto.solicitacao.SolicitacaoResponseDTO;
import com.ducktecnology.pet_register.repository.AnimalRepository;
import com.ducktecnology.pet_register.repository.AgendamentoRepository;
import com.ducktecnology.pet_register.repository.ServicoRepository;
import com.ducktecnology.pet_register.repository.VeterinarioRepository;
import com.ducktecnology.pet_register.repository.UsuarioRepository;
import com.ducktecnology.pet_register.repository.SolicitacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private static final Set<String> HORARIOS_PERMITIDOS = Set.of(
            "08:00",
            "09:00",
            "10:00",
            "11:00",
            "14:00",
            "15:00",
            "16:00",
            "17:00"
    );

    private final SolicitacaoRepository solicitacaoRepository;
    private final AnimalRepository animalRepository;
    private final ServicoRepository servicoRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;

    private Usuario usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    private void validarAcessoModeracao(Usuario usuario) {
        if (usuario.getPerfil() == Perfil.ADMIN || usuario.getPerfil() == Perfil.VETERINARIO) {
            return;
        }

        throw new RuntimeException("Sem permissão para moderar solicitações");
    }

    public SolicitacaoResponseDTO criar(SolicitacaoRequestDTO dto) {

        // Pega o usuário logado via Token JWT
        Usuario usuarioLogado = usuarioLogado();

        // Valida se o animal existe
        Animal animal = animalRepository.findById(dto.animalId())
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        Servico servico = servicoRepository.findById(dto.servicoId())
            .orElseThrow(() -> new RuntimeException("Servico não encontrado"));

        if (Boolean.FALSE.equals(servico.getAtivo())) {
            throw new RuntimeException("Servico indisponivel");
        }

        // Monta a Entidade
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setTipo(dto.tipo());
        solicitacao.setAnimal(animal);
        solicitacao.setServico(servico);
        solicitacao.setUsuario(usuarioLogado);
        solicitacao.setDataPreferencial(dto.dataPreferencial());
        solicitacao.setDescricao(dto.descricao());
        solicitacao.setContato(dto.contato());
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataCriacao(LocalDateTime.now());

        // Salva no banco
        solicitacaoRepository.save(solicitacao);

        return toResponseDTO(solicitacao);
    }

    public List<SolicitacaoResponseDTO> listarMinhas() {
        Usuario usuarioLogado = usuarioLogado();

        return solicitacaoRepository.findByUsuario(usuarioLogado).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<SolicitacaoResponseDTO> listarTodas() {
        Usuario usuarioLogado = usuarioLogado();
        validarAcessoModeracao(usuarioLogado);

        return solicitacaoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // Conversor auxiliar
    private SolicitacaoResponseDTO toResponseDTO(Solicitacao solicitacao) {
        return new SolicitacaoResponseDTO(
                solicitacao.getId(),
                solicitacao.getTipo().name(),
                solicitacao.getAnimal().getId(),
                solicitacao.getAnimal().getNome(),
                solicitacao.getServico() != null ? solicitacao.getServico().getId() : null,
                solicitacao.getServico() != null ? solicitacao.getServico().getNome() : null,
            solicitacao.getUsuario() != null ? solicitacao.getUsuario().getId() : null,
            solicitacao.getUsuario() != null ? solicitacao.getUsuario().getNome() : null,
            solicitacao.getUsuario() != null ? solicitacao.getUsuario().getCpf() : null,
                solicitacao.getDescricao(),
                solicitacao.getContato(),
                solicitacao.getStatus().name(),
                solicitacao.getDataCriacao()
        );
    }
    @Transactional
    public void atualizarStatus(
            Long id,
            StatusSolicitacao novoStatus,
            String horarioSelecionado,
            LocalDate dataSelecionada
    ) {
        Usuario usuarioLogado = usuarioLogado();
        validarAcessoModeracao(usuarioLogado);

        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        StatusSolicitacao statusAnterior = solicitacao.getStatus();
        solicitacao.setStatus(novoStatus);

        if (novoStatus == StatusSolicitacao.APROVADO && statusAnterior != StatusSolicitacao.APROVADO) {
            criarAgendamentoAutomatico(solicitacao, usuarioLogado, horarioSelecionado, dataSelecionada);
        }

        solicitacaoRepository.save(solicitacao);
    }

    private void criarAgendamentoAutomatico(
            Solicitacao solicitacao,
            Usuario usuarioLogado,
            String horarioSelecionado,
            LocalDate dataSelecionada
    ) {
        if (solicitacao.getAgendamento() != null) {
            return;
        }

        Veterinario veterinario = selecionarVeterinario(usuarioLogado);
        Servico servico = solicitacao.getServico();

        if (servico == null) {
            throw new RuntimeException("Solicitação sem serviço vinculado");
        }

        LocalDate dataBase = dataSelecionada != null
                ? dataSelecionada
                : solicitacao.getDataPreferencial() != null
                ? solicitacao.getDataPreferencial()
                : LocalDate.now().plusDays(1);

        LocalTime horarioBase = parseHorarioSelecionado(horarioSelecionado);
        LocalDateTime inicio = dataBase.atTime(horarioBase);
        int duracaoMinutos = servico.getDuracaoMinutos() != null && servico.getDuracaoMinutos() > 0
                ? servico.getDuracaoMinutos()
                : 60;
        LocalDateTime fim = inicio.plusMinutes(duracaoMinutos);

        HorarioAtendimento horario = new HorarioAtendimento();
        horario.setServico(servico);
        horario.setVeterinario(veterinario);
        horario.setDataHoraInicio(inicio);
        horario.setDataHoraFim(fim);
        horario.setStatus(StatusHorario.RESERVADO);

        Agendamento agendamento = new Agendamento();
        agendamento.setAnimal(solicitacao.getAnimal());
        agendamento.setSolicitacao(solicitacao);
        agendamento.setHorario(horario);
        agendamento.setStatus(StatusAgendamento.MARCADO);

        agendamentoRepository.save(agendamento);
        solicitacao.setAgendamento(agendamento);
    }

    private LocalTime parseHorarioSelecionado(String horarioSelecionado) {
        String horarioNormalizado = horarioSelecionado == null || horarioSelecionado.isBlank()
                ? "09:00"
                : horarioSelecionado.trim();

        if (!HORARIOS_PERMITIDOS.contains(horarioNormalizado)) {
            throw new RuntimeException("Horario invalido. Selecione uma faixa entre 08:00 e 12:00 ou entre 14:00 e 18:00.");
        }

        return LocalTime.parse(horarioNormalizado);
    }

    private Veterinario selecionarVeterinario(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == Perfil.VETERINARIO) {
            return veterinarioRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseGet(() -> criarVeterinarioDoUsuario(usuarioLogado));
        }

        return veterinarioRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> usuarioRepository.findByPerfilAndAtivoTrue(Perfil.VETERINARIO).stream()
                        .findFirst()
                        .map(this::criarVeterinarioDoUsuario)
                        .orElseThrow(() -> new RuntimeException("Nenhum veterinário cadastrado para gerar agendamento")));
    }

    private Veterinario criarVeterinarioDoUsuario(Usuario usuario) {
        if (usuario == null || usuario.getPerfil() != Perfil.VETERINARIO) {
            throw new RuntimeException("Nenhum veterinário cadastrado para gerar agendamento");
        }

        return veterinarioRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Veterinario veterinario = new Veterinario();
                    veterinario.setUsuario(usuario);
                    veterinario.setCrmv("PENDENTE");
                    return veterinarioRepository.save(veterinario);
                });
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuarioLogado = usuarioLogado();
        validarAcessoModeracao(usuarioLogado);

        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        Agendamento agendamento = solicitacao.getAgendamento();
        if (agendamento != null) {
            agendamento.setSolicitacao(null);
            solicitacao.setAgendamento(null);
            agendamentoRepository.delete(agendamento);
        }

        solicitacaoRepository.delete(solicitacao);
    }
}
