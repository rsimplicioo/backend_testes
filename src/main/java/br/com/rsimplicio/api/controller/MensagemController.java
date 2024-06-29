package br.com.rsimplicio.api.controller;

import br.com.rsimplicio.api.exception.MensagemNotFoundException;
import br.com.rsimplicio.api.model.Mensagem;
import br.com.rsimplicio.api.service.MensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Mensagem> registrarMensagem(@RequestBody Mensagem mensagem) {
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mensagemRegistrada);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> buscarMensagen(@PathVariable String id) {
        var uuid = UUID.fromString(id);
        try {
            var mensagemEncontrada = mensagemService.buscarMensagem(uuid);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(mensagemEncontrada);
        }catch (MensagemNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Mensagem>> listarMensagens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Mensagem> mensagens = mensagemService.listarMensagens(pageable);
        return new ResponseEntity<>(mensagens, HttpStatus.OK);
    }

    @PutMapping(
        value = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> alterarMensagem(@PathVariable String id, @RequestBody Mensagem mensagem) {
        var uuid = UUID.fromString(id);
        try{
            var mensagemAtualizada = mensagemService.alterarMensagem(uuid, mensagem);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(mensagemAtualizada);
        }catch (MensagemNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> excluirMensagem(@PathVariable String id) {
        var uuid = UUID.fromString(id);
        try{
            mensagemService.excluirMensagem(uuid);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("mensagem excluida com sucesso");
        }catch (MensagemNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
