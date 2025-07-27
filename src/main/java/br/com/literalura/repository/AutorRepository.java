package br.com.literalura.repository;

import br.com.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("select a from Autor a where :ano between a.anoNascimento and a.anoFalecimento")
    List<Autor> findAutoresPorAno(@Param("ano") Integer ano);

    List<Autor> findByNomeContainingIgnoreCase(String nome);
}