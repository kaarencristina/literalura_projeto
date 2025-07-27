package br.com.literalura.controller;

import br.com.literalura.model.Autor;
import br.com.literalura.model.Livro;
import br.com.literalura.repository.AutorRepository;
import br.com.literalura.repository.LivroRepository;
import br.com.literalura.service.GutendexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LiterAluraController {

    @Autowired
    private GutendexService gutendexService;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    // Opção 1: Buscar livro pelo título e salvar no banco
    @PostMapping("/buscar-livro")
    public String buscarLivro(@RequestParam String titulo) {
        Optional<Livro> livro = gutendexService.buscarLivroPorTitulo(titulo);
        if (livro.isPresent()) {
            return "Livro '" + livro.get().getTitulo() + "' salvo no banco.";
        } else {
            return "Livro não encontrado na API.";
        }
    }

    // Opção 2: Listar livros registrados
    @GetMapping("/livros")
    public List<Livro> listarLivros() {
        return livroRepository.findAll();
    }

    // Opção 3: Listar autores
    @GetMapping("/autores")
    public List<Autor> listarAutores() {
        return autorRepository.findAll();
    }

    // Opção 4: Listar autores vivos em determinado ano
    @GetMapping("/autores/ano/{ano}")
    public List<Autor> autoresPorAno(@PathVariable Integer ano) {
        return autorRepository.findAutoresPorAno(ano);
    }

    // Opção 5: Listar livros por idioma
    @GetMapping("/livros/idioma/{idioma}")
    public List<Livro> livrosPorIdioma(@PathVariable String idioma) {
        return livroRepository.findByIdioma(idioma.toLowerCase());
    }
}