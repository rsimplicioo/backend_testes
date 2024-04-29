package br.com.rsimplicio.api.service;

import br.com.rsimplicio.api.model.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MensagemService {
    Mensagem registrarMensagem(Mensagem mensagem);

    Mensagem buscarMensagem(UUID id);

    Mensagem alterarMensagem(UUID id, Mensagem mensagemModificada);

    boolean removerMensagem(UUID id);

    Page<Mensagem> listarMensagens(Pageable pageable);
}
