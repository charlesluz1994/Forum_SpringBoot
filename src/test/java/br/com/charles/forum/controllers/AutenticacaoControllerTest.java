package br.com.charles.forum.controllers;

import java.net.URI;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.charles.forum.modelo.Usuario;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
@ActiveProfiles("test")
public class AutenticacaoControllerTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private MockMvc mock;
	
	@Test
	public void shouldBeReturnValidToken() throws Exception {
		URI path = new URI("/auth");
		String json = "{\"email\": \"aluno@email.com\", \"senha\": \"123456\"}";
		
		Usuario usuario = new Usuario();
		usuario.setNome("Aluno");
		usuario.setEmail("aluno@email.com");
		usuario.setSenha("$2a$10$e0fvHjp0BoMVD0jMhyNzGOXaGODJVsu/5tz0.w1Z4ZI7utXZ06J9W");
		
		entityManager.persistAndFlush(usuario);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(path)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		ResultMatcher expectedResult = MockMvcResultMatchers.status().isOk();
		
		mock.perform(request).andExpect(expectedResult);
	}

	@Test
	public void shouldBeReturnBadRequest() throws Exception {
		URI path = new URI("/auth");
		String json = "{\"email\": \"invalido@email.com\", \"senha\": \"123456\"}";
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(path)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		ResultMatcher expectedResult = MockMvcResultMatchers.status().isBadRequest();
		
		mock.perform(request).andExpect(expectedResult);
	}
	
}
