package br.com.rsimplicio.api.service;

import br.com.rsimplicio.api.exception.MensagemNotFoundException;
import br.com.rsimplicio.api.model.Mensagem;
import br.com.rsimplicio.api.repository.MensagemRepository;
import br.com.rsimplicio.api.utils.MensagemHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MensagemServiceTest {

    private MensagemService mensagemService;

    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() throws Exception {
        mock = MockitoAnnotations.openMocks(this);
        mensagemService = new MensagemServiceImpl(mensagemRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var mensagem = MensagemHelper.gerarMensagem();
        when(mensagemRepository.save(any(Mensagem.class)))
                .thenAnswer(i -> i.getArgument(0));
        // Act
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);
        // Assert
        assertThat(mensagemRegistrada)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRegistrada.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRegistrada.getUsuario()).isEqualTo(mensagem.getUsuario());
        assertThat(mensagem.getId()).isNotNull();
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
        var id = UUID.fromString("96dd8dc2-7e90-4656-bcfe-a5985555510b");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id))
            .thenReturn(Optional.of(mensagem));
        // Act
        var mensagemObtida = mensagemService.buscarMensagem(id);
        // Assert
        assertThat(mensagemObtida).isEqualTo(mensagem);
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoBuscarMensagem_IdNaoEncontrado() {
        // Arrange
        var id = UUID.fromString("7c3036cd-5b30-4e55-9253-dde9807cb0a3");
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.empty());
        // Assert
        assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void devePermitirAlterarMensagem() {
        // Arrange
        var id = UUID.fromString("b7964f82-2e95-4725-8dbb-b73ea9ddd960");
        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = new Mensagem();
        mensagemNova.setId(mensagemAntiga.getId());
        mensagemNova.setUsuario(mensagemAntiga.getUsuario());
        mensagemNova.setConteudo("ABCD 12345");

        when(mensagemRepository.findById(id))
            .thenReturn(Optional.of(mensagemAntiga));

        when(mensagemRepository.save(any(Mensagem.class)))
            .thenAnswer(i -> i.getArgument(0));
        // Act
        var mensagemObtida = mensagemService.alterarMensagem(id, mensagemNova);
        // Assert
        assertThat(mensagemObtida).isInstanceOf(Mensagem.class).isNotNull();
        assertThat(mensagemObtida.getId()).isEqualTo(mensagemNova.getId());
        assertThat(mensagemObtida.getUsuario()).isEqualTo(mensagemNova.getUsuario());
        assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagemNova.getConteudo());
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdNaoNaoExiste() {
        // Arrange
        var id = UUID.fromString("a4d8f3d1-7496-43dc-aeac-ab8122ecd845");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagem))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
        // Arrange
        var id = UUID.fromString("a314bbb7-eb90-4e2e-8708-7bf4c05f4825");
        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = MensagemHelper.gerarMensagem();
        mensagemNova.setId(UUID.fromString("ca8b07cc-b69c-4fa7-b899-3e37ff0c8da1"));
        mensagemNova.setConteudo("ABCD 12345");

        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagemAntiga));
        // Act & Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemNova))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem atualizada não apresenta o mesmo id");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void devePermitirExcluirMensagem() {
        // Arrange
        var id = UUID.fromString("ae1baf3f-ac8b-43a0-a62d-570b02561514");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagem));
        doNothing().when(mensagemRepository).deleteById(id);
        // Act
        var mensagemFoiRemovida = mensagemService.excluirMensagem(id);
        // Assert
        assertThat(mensagemFoiRemovida).isTrue();
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoExcluirMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.fromString("88e68963-6c87-4e39-a387-9db57b718fc7");
        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> mensagemService.excluirMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void devePermitirListarMensagens() {
        // Arrange
        Page<Mensagem> listaDeMensagem = new PageImpl<>(Arrays.asList(
                MensagemHelper.gerarMensagem(),
                MensagemHelper.gerarMensagem()
        ));
        when(mensagemRepository.listarMensagens(any(Pageable.class))).thenReturn(listaDeMensagem);
        // Act
        var resultadoObtido = mensagemService.listarMensagens(Pageable.unpaged());
        // Assert
        assertThat(resultadoObtido).hasSize(2);
        assertThat(resultadoObtido.getContent())
                .asList()
                .allSatisfy(mensagem -> {
                    assertThat(mensagem)
                            .isNotNull()
                            .isInstanceOf(Mensagem.class);
                });
        verify(mensagemRepository, times(1)).listarMensagens(any(Pageable.class));
    }

}
