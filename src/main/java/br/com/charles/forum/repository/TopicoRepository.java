package br.com.charles.forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.charles.forum.modelo.Topico;

public interface TopicoRepository extends JpaRepository<Topico, Long>{

	Page<Topico> findByCursoNome(String nomeCurso, Pageable paginacao);
	
	
	/*
	 * Caso queira um nome em Português que não esteja na convenção do Spring, ele não irá gerar a query automaticamente. E com isto, você deverá gerar a query JPQL manualmente.
	 * @Query("SELECT t from Topico t where t.curso.nome = :nomeCurso") List<Topico>
	 * carregarPorNomeDoCurso(@Param("nomeCurso") String nomeCurso);
	 */
}
