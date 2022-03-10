package br.com.charles.forum.config.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.charles.forum.modelo.Usuario;
import br.com.charles.forum.repository.UsuarioRepository;

/*Essa classe consulta o usuário no banco de dados
 * Ao entrar na aplicação e clicar em login, o spring chama esta service e faz a validação. Não temos um controller para ela, é utilizado o controller do próprio spring. */
@Service
public class AutenticacaoService implements UserDetailsService{

	@Autowired
	private UsuarioRepository repository;
	
	/*o Spring não precisa de senha como parametro, pois ele faz a validação da senha diretamente em sua memória no momento que busca o e-mail.
	 * Utilizamos o Optinal pois a pesquisa pode retornar um e-mail ou não, caso não tenha, retornamos uma exception.*/
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Usuario> usuario = repository.findByEmail(username);
		
		if(usuario.isPresent()) {
			return usuario.get();
		}
		throw new UsernameNotFoundException("Dados inválidos!");
	}

}
