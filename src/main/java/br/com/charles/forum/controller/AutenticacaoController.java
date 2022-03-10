package br.com.charles.forum.controller;

import javax.naming.AuthenticationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.charles.forum.config.security.TokenService;
import br.com.charles.forum.controller.dto.TokenDto;
import br.com.charles.forum.controller.form.LoginForm;

@RestController
@RequestMapping("/auth") /*Se chegar alguma requisição com /auth, é para chamar este controller */
public class AutenticacaoController {
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private TokenService tokenService;
	
	/* Método da lógica de autenticação
	 * Precisa ser via método Post, pois irá receber os parametros de usuário e senha da aplicação cliente*/
	@PostMapping
	public ResponseEntity<TokenDto> autenticar(@RequestBody @Valid LoginForm form){
		UsernamePasswordAuthenticationToken dadosLogin = form.converter();
					
		try {
			Authentication authentication = authManager.authenticate(dadosLogin);
			String token = tokenService.gerarToken(authentication);
			System.out.println(token);
			return ResponseEntity.ok(new TokenDto(token, "Bearer"));
		} catch ( org.springframework.security.core.AuthenticationException e) {
			return ResponseEntity.badRequest().build();
		}
		
		
	}
}
