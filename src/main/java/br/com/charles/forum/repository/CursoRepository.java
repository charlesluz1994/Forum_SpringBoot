package br.com.charles.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.charles.forum.modelo.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

	 Curso findByNome(String nome);

}
