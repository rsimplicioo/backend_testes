package br.com.rsimplicio.api.controller;

import br.com.rsimplicio.api.model.Mensagem;
import br.com.rsimplicio.api.utils.MensagemHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class MensagemControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup(){
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem(){
            var mensagem = MensagemHelper.gerarMensagem();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
//                    .log().all()
            .when()
                    .post("/mensagens")
            .then()
//                    .log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML(){
            String xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Conteudo da mensagem</conteudo></mensagem>";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
            .when()
                    .post("/mensagens")
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/mensagens"));
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem(){
            var id = "4a3679eb-5f35-497f-b113-fae19fb004ce";

            when()
                    .get("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoEncontrado(){
            var id = "3dae7b93-3e9c-4a1a-8ea8-6874fcdde324";

            when()
                    .get("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class AlterarMensagem {
        @Test
        void devePermitirAlterarMensagem(){
            var id = UUID.fromString("4a3679eb-5f35-497f-b113-fae19fb004ce");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("Miguel Garcez")
                    .conteudo("conteudo da mensagem")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
            .when()
                    .put("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoNaoExiste(){
            var id = UUID.fromString("3dae7b93-3e9c-4a1a-8ea8-6874fcdde324");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("Miguel Garcez")
                    .conteudo("conteudo da mensagem")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
            .when()
                    .put("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem não encontrada"));
//                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente(){
            var id = UUID.fromString("4a3679eb-5f35-497f-b113-fae19fb004ce");
            var mensagem = Mensagem.builder()
                    .id(UUID.fromString("4a3679eb-5f35-497f-b113-fae19fb004c"))
                    .usuario("Miguel Garcez")
                    .conteudo("conteudo da mensagem")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
            .when()
                    .put("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem atualizada não apresenta o mesmo id"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_ApresentaPayloadComXML(){
            var id = UUID.fromString("2692fbe2-0b93-4da8-8fde-bb53e0ec57f5");
            String xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Conteudo da mensagem</conteudo></mensagem>";

            given()
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                    .body(xmlPayload)
            .when()
                    .put("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }
    }

    @Nested
    class ExcluirMensagem {
        @Test
        void devePermitirExcluirMensagem(){
            var id = UUID.fromString("1d50cccf-e4e0-4a83-bac4-449d6693cfd1");

            when()
                    .delete("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo("mensagem excluida com sucesso"));
        }

        @Test
        void deveGerarExcecao_QuandoExcluirMensagem_IdNaoExiste(){
            var id = UUID.fromString("4a3679eb-5f35-497f-b113-fae19fb004c");

            when()
                    .delete("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem não encontrada"));
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens(){

            given()
                    .queryParam("page", "0")
                    .queryParam("size", "10")
            .when()
                    .get("/mensagens")
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoInformadoPaginacao(){
            when()
                    .get("/mensagens")
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));
        }
    }
}
