package br.com.rsimplicio.api.repository;

import br.com.rsimplicio.api.model.Mensagem;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MensagemRepository extends JpaRepository<Mensagem, UUID> {
    @Query("SELECT m FROM Mensagem m ORDER BY m.dataCriacao DESC")
    Page<Mensagem> listarMensagens(Pageable pageable);
}
