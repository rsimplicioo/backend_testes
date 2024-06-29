package br.com.rsimplicio.api.repository;

import br.com.rsimplicio.api.model.Mensagem;
import br.com.rsimplicio.api.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela() {
        var totalDeRegistros = mensagemRepository.count();
        assertThat(totalDeRegistros).isPositive();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);

        // Act
        var mensagemRecebida = mensagemRepository.save(mensagem);

        // Assert
        assertThat(mensagemRecebida)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRecebida.getId()).isEqualTo(id);
        assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRecebida.getUsuario()).isEqualTo(mensagem.getUsuario());
    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
        var id = UUID.fromString("4a3679eb-5f35-497f-b113-fae19fb004ce");

        // Act
        var mensagemRecebidaOpcional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOpcional)
                .isPresent();
        mensagemRecebidaOpcional.ifPresent(mensagemRecebida -> {
            assertThat(mensagemRecebida.getId()).isEqualTo(id);
        });
    }

    @Test
    void devePermitirExcluirMensagem() {
        // Arrange
        var id = UUID.fromString("1d50cccf-e4e0-4a83-bac4-449d6693cfd1");

        // Act
        mensagemRepository.deleteById(id);
        var mensagemRecebidaOpcional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOpcional).isEmpty();
    }

    @Test
    void devePermitirListarMensagem() {
        // Act
        var resultadosObtidos = mensagemRepository.findAll();
        // Assert
        assertThat(resultadosObtidos).hasSizeGreaterThan(0);
    }
}
