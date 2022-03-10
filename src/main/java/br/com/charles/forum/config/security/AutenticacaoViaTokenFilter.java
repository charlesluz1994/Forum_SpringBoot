package br.com.charles.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.charles.forum.modelo.Usuario;
import br.com.charles.forum.repository.UsuarioRepository;

/*Este filtro intercepta a requisição e executa o filtro de token */
public class AutenticacaoViaTokenFilter extends OncePerRequestFilter{

	
	private TokenService tokenService;
	private UsuarioRepository repository;
	
	
	//Classe filtro não tem injeção(Autowired), então é necessario injetar o token pelo construtor.
	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository repository) {
		this.tokenService = tokenService;
	}

	/*Neste método é inserida a lógica para pegar o token do cabeçalho, verifica se esta ok, se estiver ok irá ser autenticado o usuário no Spring  */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = recuperarToken(request);
		boolean valido = tokenService.isTokenValido(token);
		if(valido) {
			autenticarCliente(token);
		}
		
		System.out.println(token);
		filterChain.doFilter(request, response);
	}

	/*Já tenho o usuário e senha, agora autentica! */
	private void autenticarCliente(String token) {
		Long idUsuario = tokenService.getIdUsuario(token); //Pegar o id do token
		Usuario usuario = repository.findById(idUsuario).get(); //Recuperar objeto usuário, passando o Id.
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()); //Null seria a senha, mas como já esta autenticado não faz diferença.
		SecurityContextHolder.getContext().setAuthentication(authentication); //Classe que força a autenticação para cada um.
		
	}

	private String recuperarToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token == null ||token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}
		return token.substring(7, token.length());
	}
}
