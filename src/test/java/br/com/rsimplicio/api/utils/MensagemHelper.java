package br.com.rsimplicio.api.utils;

import br.com.rsimplicio.api.model.Mensagem;

public abstract class MensagemHelper {
    public static Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("José")
                .conteudo("conteúdo da mensagem")
                .build();
    }
}
