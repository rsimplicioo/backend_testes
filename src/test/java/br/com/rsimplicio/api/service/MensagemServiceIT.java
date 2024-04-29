package br.com.rsimplicio.api.service;

import br.com.rsimplicio.api.repository.MensagemRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MensagemServiceIT {

    @Autowired
    private MensagemRepository mensagemRepository;
    @Autowired
    private MensagemService mensagemService;

    @Test
    void devePermitirRegistrarMensagem() {
        fail("Teste não implementado");
    }

    @Test
    void devePermitirBuscarMensagem() {
        fail("Teste não implementado");
    }

    @Test
    void deveGerarExcecao_QuandoBuscarMensagem_IdNaoEncontrado() {
        fail("Teste não implementado");
    }

    @Test
    void devePermitirAlterarMensagem() {
        fail("Teste não implementado");
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdNaoNaoExiste() {
        fail("Teste não implementado");
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
        fail("Teste não implementado");
    }

    @Test
    void devePermitirExcluirMensagem() {
        fail("Teste não implementado");
    }

    @Test
    void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
        fail("Teste não implementado");
    }

    @Test
    void devePermitirListarMensagens() {
        fail("Teste não implementado");
    }
}
