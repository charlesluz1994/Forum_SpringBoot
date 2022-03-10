package br.com.charles.forum.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.charles.forum.modelo.Curso;
import br.com.charles.forum.modelo.Perfil;
import br.com.charles.forum.modelo.Topico;
import br.com.charles.forum.modelo.Usuario;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TopicosControllerTest {

	private URI path;
	private MockHttpServletRequestBuilder request;
	private ResultMatcher expectedResult;

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private MockMvc mock;

	@Before
	public void setUp() throws Exception {
		path = new URI("/topicos");
		
		Perfil perfilAluno = new Perfil();
		Perfil perfilModerador = new Perfil();

		perfilAluno.setNome("ROLE_ALUNO");
		perfilModerador.setNome("ROLE_MODERADOR");
		
		entityManager.persistAndFlush(perfilAluno);
		entityManager.persistAndFlush(perfilModerador);
		
		List<Perfil> perfis = new ArrayList<Perfil>();
		
		perfis.add(perfilModerador);

		Usuario usuario1 = new Usuario();
		Usuario usuario2 = new Usuario();

		usuario1.setNome("Aluno");
		usuario1.setEmail("aluno@email.com");
		usuario1.setSenha("$2a$10$e0fvHjp0BoMVD0jMhyNzGOXaGODJVsu/5tz0.w1Z4ZI7utXZ06J9W");

		usuario2.setNome("Moderador");
		usuario2.setEmail("moderador@email.com");
		usuario2.setSenha("$2a$10$e0fvHjp0BoMVD0jMhyNzGOXaGODJVsu/5tz0.w1Z4ZI7utXZ06J9W");
		//usuario2.setPerfis(perfis);

		entityManager.persistAndFlush(usuario1);
		entityManager.persistAndFlush(usuario2);

		Curso springBoot = new Curso();
		Curso html5 = new Curso();

		springBoot.setNome("Spring Boot");
		springBoot.setCategoria("Programação");

		html5.setNome("HTML 5");
		html5.setCategoria("Front-end");

		entityManager.persistAndFlush(springBoot);
		entityManager.persistAndFlush(html5);

		Topico topico1 = new Topico("Bigode", "Error ao criar projeto", springBoot);
		Topico topico2 = new Topico("Bigorna", "Projeto não compila", springBoot);
		Topico topico3 = new Topico("Bigodinho", "Tag HTML", html5);

		topico1.setAutor(usuario1);
		topico2.setAutor(usuario2);
		topico3.setAutor(usuario1);

		entityManager.persistAndFlush(topico1);
		entityManager.persistAndFlush(topico2);
		entityManager.persistAndFlush(topico3);
	}

	@Test
	public void testListaTopicos() throws Exception {
		Assert.assertNotNull(path);

		request = MockMvcRequestBuilders.get(path);
		expectedResult = MockMvcResultMatchers.status().isOk();

		String response = mock.perform(request).andExpect(expectedResult).andReturn().getResponse()
				.getContentAsString();

		Assert.assertNotNull(response);
		Assert.assertTrue(response.contains("content"));
	}

	@Test
	public void testDetalhar() throws Exception {
		request = MockMvcRequestBuilders.get("/topicos/1");
		expectedResult = MockMvcResultMatchers.status().isOk();

		String response = mock.perform(request).andExpect(expectedResult).andReturn().getResponse()
				.getContentAsString();

		Assert.assertNotNull(response);
		Assert.assertTrue(response.contains("NAO_RESPONDIDO"));
	}

	@Test
	public void testCadastrar() throws Exception {
		String json = "{\"email\": \"aluno@email.com\", \"senha\": \"123456\"}";
		path = new URI("/auth");

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(path)
				.contentType(MediaType.APPLICATION_JSON).content(json);

		expectedResult = MockMvcResultMatchers.status().isOk();

		String response = mock.perform(request).andExpect(expectedResult).andReturn().getResponse()
				.getContentAsString();

		JSONObject data = new JSONObject(response);
		String jwtToken = data.getString("token");
		
		json = "{\"titulo\": \"Cache\", \"mensagem\": \"Invalidando cache\", \"nomeCurso\": \"Spring Boot\"}";

		path = new URI("/topicos");

		request = MockMvcRequestBuilders.post(path).content(json).header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + jwtToken);
		
		expectedResult = MockMvcResultMatchers.status().isCreated();

		mock.perform(request).andExpect(expectedResult);
	}

	@Test
	public void testAtualizar() throws Exception {
		String json = "{\"email\": \"aluno@email.com\", \"senha\": \"123456\"}";
		path = new URI("/auth");

		request = MockMvcRequestBuilders.post(path).contentType(MediaType.APPLICATION_JSON).content(json);

		expectedResult = MockMvcResultMatchers.status().isOk();

		String response = mock.perform(request).andExpect(expectedResult).andReturn().getResponse()
				.getContentAsString();
		
		JSONObject data = new JSONObject(response);
		String jwtToken = data.getString("token");

		json = "{\"titulo\": \"Duvida 3 atualizada\", \"mensagem\": \"Tag HTML nova\"}";

		request = MockMvcRequestBuilders.put("/topicos/3").content(json).header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + jwtToken);
		
		expectedResult = MockMvcResultMatchers.status().isOk();

		mock.perform(request).andExpect(expectedResult);
	}

	@Test
	public void testRemover() throws Exception {
		String json = "{\"email\": \"moderador@email.com\", \"senha\": \"123456\"}";
		path = new URI("/auth");

		request = MockMvcRequestBuilders.post(path).contentType(MediaType.APPLICATION_JSON).content(json);

		expectedResult = MockMvcResultMatchers.status().isOk();

		String response = mock.perform(request).andExpect(expectedResult).andReturn().getResponse()
				.getContentAsString();
		
		JSONObject data = new JSONObject(response);
		String jwtToken = data.getString("token");

		request = MockMvcRequestBuilders.delete("/topicos/1").header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + jwtToken);
		
		expectedResult = MockMvcResultMatchers.status().isOk();

		mock.perform(request).andExpect(expectedResult);
	}

}