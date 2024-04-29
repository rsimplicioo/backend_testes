package br.com.rsimplicio.api.service;

import br.com.rsimplicio.api.exception.MensagemNotFoundException;
import br.com.rsimplicio.api.model.Mensagem;
import br.com.rsimplicio.api.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MensagemServiceImpl implements MensagemService {
    private final MensagemRepository mensagemRepository;

    @Autowired
    public MensagemServiceImpl(MensagemRepository mensagemRepository) {
        this.mensagemRepository = mensagemRepository;
    }

    @Override
    public Mensagem registrarMensagem(Mensagem mensagem) {
        mensagem.setId(UUID.randomUUID());
        return mensagemRepository.save(mensagem);
    }

    @Override
    public Mensagem buscarMensagem(UUID id) {
        return mensagemRepository.findById(id).orElseThrow(() -> new MensagemNotFoundException("Mensagem não encontrada"));
    }

    @Override
    public Mensagem alterarMensagem(UUID id, Mensagem mensagemAtualizada) {
        var mensagem = this.buscarMensagem(id);
        if(!mensagem.getId().equals(mensagemAtualizada.getId())){
            throw new MensagemNotFoundException("Mensagem atualizada não apresenta o mesmo id");
        }
        mensagem.setConteudo(mensagemAtualizada.getConteudo());
        return mensagemRepository.save(mensagem);
    }

    @Override
    public boolean removerMensagem(UUID id) {
        this.buscarMensagem(id);
        mensagemRepository.deleteById(id);
        return true;
    }

    @Override
    public Page<Mensagem> listarMensagens(Pageable pageable) {
        return mensagemRepository.listarMensagens(pageable);
    }
}
