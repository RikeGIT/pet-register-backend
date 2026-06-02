package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.model.CodigoConfirmacao;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.exception.UnauthorizedException;
import com.ducktecnology.pet_register.repository.CodigoConfirmacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final CodigoConfirmacaoRepository codigoConfirmacaoRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.otp.expiration-minutes:10}")
    private int expirationMinutes;

    @Value("${app.mail.from:no-reply@adotapatos.local}")
    private String fromAddress;

    public String gerarCodigo() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    public CodigoConfirmacao criarCodigoParaUsuario(Usuario usuario, boolean contaConfirmada) {
        codigoConfirmacaoRepository.deleteByUsuario(usuario);

        String codigoLimpo = gerarCodigo();
        CodigoConfirmacao codigo = new CodigoConfirmacao();
        codigo.setUsuario(usuario);
        codigo.setCodigoConfirmacao(passwordEncoder.encode(codigoLimpo));
        codigo.setExpiraEm(LocalDateTime.now().plusMinutes(expirationMinutes));
        codigo.setContaConfirmada(contaConfirmada);
        codigo.setCriadoEm(LocalDateTime.now());

        CodigoConfirmacao salvo = codigoConfirmacaoRepository.save(codigo);
        enviarCodigoEmail(usuario.getEmail(), codigoLimpo);
        return salvo;
    }

    public void validarCodigo(Usuario usuario, String codigoDigitado) {
        CodigoConfirmacao token = codigoConfirmacaoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new UnauthorizedException("Código de confirmação não encontrado"));

        if (token.getExpiraEm().isBefore(LocalDateTime.now())) {
            codigoConfirmacaoRepository.delete(token);
            throw new UnauthorizedException("Código de confirmação expirado");
        }

        boolean codigoValido = passwordEncoder.matches(codigoDigitado, token.getCodigoConfirmacao());
        if (!codigoValido) {
            throw new UnauthorizedException("Código de confirmação inválido");
        }

        token.setContaConfirmada(true);
        codigoConfirmacaoRepository.delete(token);
    }

    private void enviarCodigoEmail(String email, String codigo) {
        try {
                        MimeMessage message = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                        helper.setTo(email);
                        helper.setFrom(fromAddress);
                        helper.setSubject("Seu código de acesso AdotaPatos");
                        helper.setText(criarCorpoEmailHtml(codigo), true);

                        mailSender.send(message);
        } catch (Exception ex) {
            System.out.println("[OTP] Falha ao enviar e-mail para " + email + ": " + ex.getMessage());
            System.out.println("[OTP] Código gerado para teste local: " + codigo);
        }
    }

        private String criarCorpoEmailHtml(String codigo) {
                return String.format("""
                                <div style="margin:0;padding:0;background:#f6efe3;font-family:Arial,Helvetica,sans-serif;color:#18241d;">
                                    <div style="max-width:640px;margin:0 auto;padding:32px 16px;">
                                        <div style="background:linear-gradient(180deg,#1a4e41 0%%,#183f35 100%%);border-radius:24px 24px 0 0;padding:28px 32px;color:#f3f6f5;">
                                            <div style="font-size:13px;letter-spacing:.16em;text-transform:uppercase;opacity:.84;">AdotaPatos</div>
                                            <h1 style="margin:10px 0 0;font-size:28px;line-height:1.2;">Confirme seu acesso</h1>
                                            <p style="margin:10px 0 0;font-size:15px;line-height:1.6;opacity:.92;">Use o código abaixo para concluir seu login ou confirmar seu cadastro com segurança.</p>
                                        </div>

                                        <div style="background:#ffffff;border:1px solid #d8ccb8;border-top:0;border-radius:0 0 24px 24px;padding:32px;box-shadow:0 20px 40px rgba(24,36,29,.08);">
                                            <div style="text-align:center;padding:18px 16px;border-radius:20px;background:linear-gradient(180deg,rgba(31,93,77,.08),rgba(255,255,255,.95));border:1px solid rgba(31,93,77,.12);">
                                                <div style="font-size:12px;letter-spacing:.18em;text-transform:uppercase;color:#607164;font-weight:700;">Seu código de verificação</div>
                                                <div style="margin:18px 0 10px;font-size:40px;line-height:1;font-weight:800;letter-spacing:.18em;color:#1f5d4d;">%s</div>
                                                <div style="font-size:14px;color:#506357;">Válido por %d minutos.</div>
                                            </div>

                                            <div style="margin-top:24px;font-size:15px;line-height:1.7;color:#25352d;">
                                                <p style="margin:0 0 12px;">Se você solicitou este acesso, basta inserir o código acima na tela de verificação.</p>
                                                <p style="margin:0;">Se não foi você, pode ignorar este e-mail com segurança.</p>
                                            </div>

                                            <div style="margin-top:26px;padding-top:18px;border-top:1px solid #e6dccb;font-size:12px;line-height:1.6;color:#7b857f;">
                                                <strong style="color:#1f5d4d;">Dica:</strong> mantenha este código em sigilo e evite compartilhá-lo com terceiros.
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                """, codigo, expirationMinutes);
        }
}
