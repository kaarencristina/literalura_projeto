package br.com.literalura.service;

import br.com.literalura.model.Autor;
import br.com.literalura.model.Livro;
import br.com.literalura.repository.AutorRepository;
import br.com.literalura.repository.LivroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GutendexService {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://gutendex.com/books/?search=";

    public Optional<Livro> buscarLivroPorTitulo(String tituloBusca) {
        String url = BASE_URL + tituloBusca.replace(" ", "+");
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode results = root.path("results");
                if (results.isArray() && results.size() > 0) {
                    JsonNode bookNode = results.get(0);

                    String titulo = bookNode.path("title").asText();
                    String idioma = bookNode.path("languages").get(0).asText();
                    int downloads = bookNode.path("download_count").asInt();

                    // Autor (pegando o primeiro autor)
                    JsonNode authors = bookNode.path("authors");
                    String nomeAutor = "Autor Desconhecido";
                    Integer anoNascimento = null;
                    Integer anoFalecimento = null;
                    if (authors.isArray() && authors.size() > 0) {
                        JsonNode author = authors.get(0);
                        nomeAutor = author.path("name").asText();
                        if (!author.path("birth_year").isNull())
                            anoNascimento = author.path("birth_year").asInt();
                        if (!author.path("death_year").isNull())
                            anoFalecimento = author.path("death_year").asInt();
                    }

                    // Verificar se autor já existe no banco
                    Autor autor = autorRepository.findByNomeContainingIgnoreCase(nomeAutor)
                            .stream().findFirst().orElse(null);

                    if (autor == null) {
                        autor = new Autor(nomeAutor, anoNascimento, anoFalecimento);
                        autorRepository.save(autor);
                    }

                    Livro livro = new Livro(titulo, idioma, downloads, autor);
                    livroRepository.save(livro);
                    return Optional.of(livro);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}