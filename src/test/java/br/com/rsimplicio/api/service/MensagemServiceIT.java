package br.com.rsimplicio.api.service;

import br.com.rsimplicio.api.exception.MensagemNotFoundException;
import br.com.rsimplicio.api.model.Mensagem;
import br.com.rsimplicio.api.repository.MensagemRepository;
import br.com.rsimplicio.api.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class MensagemServiceIT {

    @Autowired
    private MensagemRepository mensagemRepository;
    @Autowired
    private MensagemService mensagemService;

    @Test
    void devePermitirRegistrarMensagem() {
        var mensagem = MensagemHelper.gerarMensagem();
        var resultadoObtido = mensagemService.registrarMensagem(mensagem);

        assertThat(resultadoObtido)
                .isNotNull()
                .isInstanceOf(Mensagem.class);
        assertThat(resultadoObtido.getId()).isNotNull();
        assertThat(resultadoObtido.getDataCriacao()).isNotNull();
        assertThat(resultadoObtido.getGostei()).isZero();
    }

    @Test
    void devePermitirBuscarMensagem() {
        var id = UUID.fromString("47aedc7b-972f-4ab5-8d4c-43bace07df5d");
        var resultadoObtido = mensagemService.buscarMensagem(id);

        assertThat(resultadoObtido)
                .isNotNull()
                .isInstanceOf(Mensagem.class);
        assertThat(resultadoObtido.getId())
                .isNotNull()
                .isEqualTo(id);
        assertThat(resultadoObtido.getUsuario())
                .isNotNull()
                .isEqualTo("Jessica");
        assertThat(resultadoObtido.getConteudo())
                .isNotNull()
                .isEqualTo("Conteudo da mensagem 02");
        assertThat(resultadoObtido.getDataCriacao()).isNotNull();
        assertThat(resultadoObtido.getGostei()).isZero();
    }

    @Test
    void deveGerarExcecao_QuandoBuscarMensagem_IdNaoEncontrado() {
        var id = UUID.fromString("3243c938-2623-4cf0-8a24-5669d69c03b4");

        assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
    }

    @Test
    void devePermitirAlterarMensagem() {
        var id = UUID.fromString("4a3679eb-5f35-497f-b113-fae19fb004ce");
        var mensagemAtualizada = MensagemHelper.gerarMensagem();
        mensagemAtualizada.setId(id);

        var resultadoObtido = mensagemService.alterarMensagem(id, mensagemAtualizada);

        assertThat(resultadoObtido.getId()).isEqualTo(id);
        assertThat(resultadoObtido.getConteudo()).isEqualTo(mensagemAtualizada.getConteudo());

        assertThat(resultadoObtido.getUsuario()).isNotEqualTo(mensagemAtualizada.getUsuario());
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdNaoNaoExiste() {
        var id = UUID.fromString("a5cc532b-7521-4d50-9e51-ed61f37fd30d");
        var mensagemAtualizada = MensagemHelper.gerarMensagem();
        mensagemAtualizada.setId(id);

        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
        var id = UUID.fromString("47aedc7b-972f-4ab5-8d4c-43bace07df5d");
        var mensagemAtualizada = MensagemHelper.gerarMensagem();
        mensagemAtualizada.setId(UUID.fromString("a3d8d302-520d-4548-abbf-e876db3647b1"));

        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem atualizada não apresenta o mesmo id");
    }

    @Test
    void devePermitirExcluirMensagem() {
        var id = UUID.fromString("1d50cccf-e4e0-4a83-bac4-449d6693cfd1");

        var resultadoObtido = mensagemService.removerMensagem(id);

        assertThat(resultadoObtido).isTrue();
    }

    @Test
    void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
        var id = UUID.fromString("953e27be-573a-483e-b3a6-2f030b26cbc2");

        assertThatThrownBy(() -> mensagemService.removerMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
    }

    @Test
    void devePermitirListarMensagens() {
        Page<Mensagem> listaDeMensagensObtida = mensagemService.listarMensagens(Pageable.unpaged());

        assertThat(listaDeMensagensObtida).hasSize(3);
        assertThat(listaDeMensagensObtida.getContent())
                .asList()
                .allSatisfy(mensagemObtida -> {
                    assertThat(mensagemObtida).isNotNull();
                });
    }
}
