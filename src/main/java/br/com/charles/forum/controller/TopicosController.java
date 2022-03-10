package br.com.charles.forum.controller;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.charles.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.charles.forum.controller.dto.TopicoDto;
import br.com.charles.forum.controller.form.AtualizacaoTopicoForm;
import br.com.charles.forum.controller.form.TopicoForm;
import br.com.charles.forum.modelo.Topico;
import br.com.charles.forum.repository.CursoRepository;
import br.com.charles.forum.repository.TopicoRepository;

/*A anotação @Controller deve vir seguidamente com a anotação @ResponseBody, pois ela que faz o direcionamento para outra página.
Porém neste caso, trata-se de um método que vai retornar o body, então utilizamos a @RestController, que já devolve o body automáticamente*/

//Obs: Ficou definido que Form, serão os dados que chegam do cliente para a API, e DTO são dados que saem da API para o cliente

@RestController
@RequestMapping("/topicos")
public class TopicosController {

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private CursoRepository cursoRepository;

	@GetMapping
	@Cacheable(value="listaDeTopicos") //identificador único deste cache setado como listaDeTopicos
	public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso, 
			@PageableDefault(sort="id", direction = Direction.DESC, page=0, size=10) Pageable paginacao) {
		
		
		if (nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
			return TopicoDto.converter(topicos);
		}

	}

	@PostMapping
	@Transactional
	@CacheEvict(value="listaDeTopicos", allEntries=true)
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);

		java.net.URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id) {
		Optional<Topico> topico = topicoRepository.findById(id);
		if (topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesDoTopicoDto(topico.get()));
		}
		return ResponseEntity.notFound().build();
	}

	@PutMapping({ "/id" })
	@Transactional
	@CacheEvict(value="listaDeTopicos", allEntries=true)
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form) {
		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) {
			Topico topico = form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value="listaDeTopicos", allEntries=true)
	public ResponseEntity<?> remover(@PathVariable Long id) {
		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
}
