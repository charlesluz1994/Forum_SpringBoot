package br.com.charles.forum.repositories;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.charles.forum.modelo.Curso;
import br.com.charles.forum.repository.CursoRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
// Configuração para utilizar o database "original" sem substituir por um em memória
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class CursoRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private CursoRepository repository;

	@Test
	public void shouldBeLoadCourseByName() {
		String courseName = "HTML 5";
		Curso html5 = new Curso();

		html5.setNome(courseName);
		html5.setCategoria("Programação");
		
		entityManager.persist(html5);
		
		Curso curso = repository.findByNome(courseName);
		
		Assert.assertNotNull(curso);
		Assert.assertEquals(courseName, curso.getNome());
	}
	
	@Test
	public void shouldBeNotLoadCourseByName() {
		String courseName = "JPA";
		Curso curso = repository.findByNome(courseName);
		
		Assert.assertNull(curso);
	}

}