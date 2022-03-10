package br.com.charles.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.charles.forum.repository.UsuarioRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AutenticacaoService AutenticacaoService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
	//Configurações de Autenticação(login,controle de acesso)
	//BCrypt é uma classe do spring security que implementa a geração do algoritmo de Hash da senha
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(AutenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
		
	}
	
	/*Configurações de Autorização(url, quem pode acessar cada url, perfil, etc..)
	.antMatchers(HttpMethod.GET, "/topicos").permitAll() => LISTA TODOS TÓPICOS - Restringe que será liberado somente o método que for selecionado, neste caso o GET. 
	.antMatchers(HttpMethod.GET, "/topicos/*").permitAll(); => LISTA UM TÓPICO ESPECÍFICO - Neste caso, o * é identificado como o ID, ele será liberado para consultar um ID especifíco. 
	.antMatchers("/topicos").permitAll() => significa que tudo que vier através de /tópicos deve ser liberado, independente do método(gt,post,put,delete) será liberado.
	.anyRequest().authenticated(); = > Qualquer outra requisição deverá ser autenticada 
	.and().csrf().disable() => como a autenticação é via token, não precisamos se previnir deste ataque hacker para aplicações web(csrf - cross site request forgery)
	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) => informamos ao spring security que quando for feita autenticação, não é para criar sessão. Será Steteless
	 * */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/topicos").permitAll() 
		.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
		.antMatchers(HttpMethod.POST, "/auth").permitAll()
		.antMatchers(HttpMethod.GET, "/actuator/**").permitAll()
		.anyRequest().authenticated()
		.and().csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService,usuarioRepository), UsernamePasswordAuthenticationFilter.class); //Antes de fazer a configuração, rode o filtro para pegar o token.
	}
	
	//Configurations de recursos estaticos(js,css,imagens,etc..)
	//Se esta aplicação estivesse integrada com algum SPA, essa configuração serve ensinar que as requisições de css, imagens,swagger,etc... deveriam ser ignoradas e não interceptar.
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
        .antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**");
	}
	
	//Gerar criptografia para senha.
	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("123456"));
	}
}
