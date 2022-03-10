package br.com.charles.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.charles.forum.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
		//Fazer a busca no banco de dados por e-mail, recebe um e-mail e devolve um usuário. Utilizando o Optional, pois o e-mail pode não existir na base de dados.
		Optional<Usuario> findByEmail(String email);
}
