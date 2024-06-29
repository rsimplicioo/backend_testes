package br.com.rsimplicio.api.controller;

import br.com.rsimplicio.api.exception.MensagemNotFoundException;
import br.com.rsimplicio.api.model.Mensagem;
import br.com.rsimplicio.api.service.MensagemService;
import br.com.rsimplicio.api.utils.MensagemHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup(){
        mock = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                })
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class RegistrarMensagem{
        @Test
        void devePermitirRegistrarMensagem() throws Exception {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.registrarMensagem(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));
            // Act & Assert
            mockMvc.perform(post("/mensagens")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mensagem))
            ).andExpect(status().isCreated());
            verify(mensagemService, times(1)).registrarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() throws Exception {
            String xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Conteudo da mensagem</conteudo></mensagem>";
            // Act & Assert
            mockMvc.perform(post("/mensagens")
                    .contentType(MediaType.APPLICATION_XML)
                    .content(xmlPayload)
            ).andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).registrarMensagem(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem{
        @Test
        void devePermitirBuscarMensagem() throws Exception {
            // Arrange
            var id = UUID.fromString("2b18bacd-10bf-4017-bf75-1325fb249e58");
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenReturn(mensagem);
            // Act & Assert
            mockMvc.perform(get("/mensagens/{id}", id))
                    .andExpect(status().isOk());
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoEncontrado() throws Exception {
            // Arrange
            var id = UUID.fromString("d2b35d8f-2e8e-4768-9aa9-42fdb4a015f7");
            when(mensagemService.buscarMensagem(id))
                .thenThrow(MensagemNotFoundException.class);
            // Act & Assert
            mockMvc.perform(get("/mensagens/{id}", id))
                    .andExpect(status().isBadRequest());
            verify(mensagemService, times(1)).buscarMensagem(id);
        }
    }

    @Nested
    class AlterarMensagem{
        @Test
        void devePermitirAlterarMensagem() throws Exception {
            // Arrange
            var id = UUID.fromString("2692fbe2-0b93-4da8-8fde-bb53e0ec57f5");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);

            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenAnswer(i -> i.getArgument(1));

            mockMvc.perform(put("/mensagens/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mensagem)))
                    .andExpect(status().isAccepted());
            verify(mensagemService, times(1)).alterarMensagem(id, mensagem);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_ApresentaPayloadComXML() throws Exception {
            var id = UUID.fromString("2692fbe2-0b93-4da8-8fde-bb53e0ec57f5");
            String xmlPayload =
                    "<mensagem>" +
                        "<id>"+id.toString()+"</id>" +
                        "<usuario>Ana</usuario>" +
                        "<conteudo>Conteudo da mensagem</conteudo>" +
                    "</mensagem>";
            // Act & Assert
            mockMvc.perform(put("/mensagens/{id}", id)
                    .contentType(MediaType.APPLICATION_XML)
                    .content(xmlPayload)
            ).andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoNaoExiste() throws Exception {
            // Arrange
            var id = UUID.fromString("b09ab50b-a7ca-45bc-a0e8-1655bc59c3f9");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);
            var conteudoDaExcecao = "Mensagem não encontrada";

            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenThrow(new MensagemNotFoundException(conteudoDaExcecao));

            mockMvc.perform(put("/mensagens/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mensagem)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExcecao));
            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() throws Exception {
            // Arrange
            var id = UUID.fromString("b09ab50b-a7ca-45bc-a0e8-1655bc59c3f9");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.fromString("13735fd3-6584-46a0-86b8-004bc8257227"));
            var conteudoDaExcecao = "mensagem atualizada não apresenta o ID correto";

            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenThrow(new MensagemNotFoundException(conteudoDaExcecao));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExcecao));
            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }
    }

    @Nested
    class ExcluirMensagem{
        @Test
        void devePermitirExcluirMensagem() throws Exception {
            var id = UUID.fromString("b9dc86ce-386d-4e99-9074-9b529e809df7");

            when(mensagemService.excluirMensagem(id)).thenReturn(true);

            mockMvc.perform(delete("/mensagens/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().string("mensagem excluida com sucesso"));

            verify(mensagemService, times(1)).excluirMensagem(id);
        }

        @Test
        void deveGerarExcecao_QuandoExcluirMensagem_IdNaoExiste() throws Exception {
            var id = UUID.fromString("de4b8591-176b-4d87-ac6a-3ea2c3115d0d");
            var mensagemDaExcecao = "Mensagem não encontrada";

            when(mensagemService.excluirMensagem(id)).thenThrow(new MensagemNotFoundException(mensagemDaExcecao));

            mockMvc.perform(delete("/mensagens/{id}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(mensagemDaExcecao));

            verify(mensagemService, times(1)).excluirMensagem(id);
        }
    }

    @Nested
    class ListarMensagens {

        @Test
        void devePermitirListarMensagens() throws Exception {
            var mensagem = MensagemHelper.gerarMensagem();
            var page = new PageImpl<>(Collections.singletonList(
                mensagem
            ));

            when(mensagemService.listarMensagens(any(Pageable.class)))
                .thenReturn(page);

            mockMvc.perform(get("/mensagens")
                .param("page", "0")
                .param("size", "10")
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", not(empty())))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoInformadoPaginacao() throws Exception {
            var mensagem = MensagemHelper.gerarMensagem();
            var page = new PageImpl<>(Collections.singletonList(
                    mensagem
            ));

            when(mensagemService.listarMensagens(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/mensagens"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", not(empty())))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(object);
    }
}
