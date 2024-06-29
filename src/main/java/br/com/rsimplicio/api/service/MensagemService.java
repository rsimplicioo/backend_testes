
package br.com.rsimplicio.api.service;

import br.com.rsimplicio.api.model.Mensagem;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * TBD.
 */
public interface MensagemService {
    Mensagem registrarMensagem(Mensagem mensagem);

    Mensagem buscarMensagem(UUID id);

    Mensagem alterarMensagem(UUID id, Mensagem mensagemModificada);

    boolean excluirMensagem(UUID id);

    Page<Mensagem> listarMensagens(Pageable pageable);
}
