package br.com.rsimplicio.api.repository;

import br.com.rsimplicio.api.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MensagemRepository extends JpaRepository<Mensagem, UUID> {

}
