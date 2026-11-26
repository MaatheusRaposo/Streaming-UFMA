package com.lab_prog.streaming.importacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.lab_prog.streaming.model.entities.Filme;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.repositories.FilmeRepository;

import tools.jackson.databind.json.JsonMapper;

/**
 * Importa o catálogo extraído pelo crawler do UIRA (um JSON por linha) para o banco.
 *
 * Roda só com o profile "import" e termina sozinho no fim:
 *   docker compose run --rm importador
 *
 * Cria ou atualiza cada filme pela chave (origem, idExterno). Filmes cujo hash não mudou são pulados,
 * então rodar de novo é rápido e não duplica nada. Nada é apagado: um filme que sumiu do UIRA continua aqui.
 */
@Component
@Profile("import")
public class ImportadorCatalogo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ImportadorCatalogo.class);
    private static final int TAMANHO_LOTE = 50;

    private final FilmeRepository filmeRepository;
    private final TransactionTemplate transacao;
    private final JsonMapper jsonMapper;
    private final ApplicationContext contexto;
    private final Path arquivo;

    public ImportadorCatalogo(
            FilmeRepository filmeRepository,
            PlatformTransactionManager transactionManager,
            JsonMapper jsonMapper,
            ApplicationContext contexto,
            @Value("${importacao.arquivo}") String arquivo) {
        this.filmeRepository = filmeRepository;
        this.transacao = new TransactionTemplate(transactionManager);
        this.jsonMapper = jsonMapper;
        this.contexto = contexto;
        this.arquivo = Path.of(arquivo);
    }

    static final class Resumo {
        int criados;
        int atualizados;
        int iguais;
        final Map<String, String> erros = new LinkedHashMap<>();
    }

    @Override
    public void run(String... args) throws IOException {
        long inicio = System.currentTimeMillis();
        Resumo resumo = new Resumo();
        List<ItemCatalogo> itens = lerArquivo(resumo);
        log.info("Importando {} filmes de {}", itens.size(), arquivo);

        for (int i = 0; i < itens.size(); i += TAMANHO_LOTE) {
            List<ItemCatalogo> lote = itens.subList(i, Math.min(i + TAMANHO_LOTE, itens.size()));
            importarLote(lote, resumo);
            log.info("  {}/{} processados", Math.min(i + TAMANHO_LOTE, itens.size()), itens.size());
        }

        log.info("Importação concluída em {}s: {} criados, {} atualizados, {} sem mudança, {} com erro",
                (System.currentTimeMillis() - inicio) / 1000, resumo.criados, resumo.atualizados,
                resumo.iguais, resumo.erros.size());
        resumo.erros.forEach((id, erro) -> log.error("  erro em {}: {}", id, erro));

        int codigo = resumo.erros.isEmpty() ? 0 : 2;
        System.exit(SpringApplication.exit(contexto, () -> codigo));
    }

    private List<ItemCatalogo> lerArquivo(Resumo resumo) throws IOException {
        if (!Files.isRegularFile(arquivo)) {
            throw new IllegalStateException("Arquivo não encontrado: " + arquivo
                    + ". Rode antes: docker compose run --rm crawler extrair");
        }
        List<ItemCatalogo> itens = new ArrayList<>();
        try (BufferedReader leitor = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            int numero = 0;
            while ((linha = leitor.readLine()) != null) {
                numero++;
                if (linha.isBlank()) continue;
                try {
                    ItemCatalogo item = jsonMapper.readValue(linha, ItemCatalogo.class);
                    validar(item);
                    itens.add(item);
                } catch (RuntimeException e) {
                    resumo.erros.put("linha " + numero, e.getMessage());
                }
            }
        }
        return itens;
    }

    private static void validar(ItemCatalogo item) {
        if (item.origem() == null || item.idExterno() == null || item.hash() == null) {
            throw new IllegalArgumentException("origem, idExterno e hash são obrigatórios");
        }
        if (!"FILME".equals(item.tipo())) {
            throw new IllegalArgumentException("tipo ainda não suportado: " + item.tipo());
        }
    }

    /**
     * Um lote por transação (com inserts em batch) deixa a importação rápida mesmo com o banco longe.
     * Se o lote falhar, refaz item por item para isolar o problemático e salvar o resto.
     */
    private void importarLote(List<ItemCatalogo> lote, Resumo resumo) {
        Resumo parcial = new Resumo();
        try {
            transacao.executeWithoutResult(status -> aplicar(lote, parcial));
            somar(resumo, parcial);
        } catch (RuntimeException erroLote) {
            log.warn("Lote falhou ({}); importando item por item", erroLote.getMessage());
            for (ItemCatalogo item : lote) {
                Resumo unitario = new Resumo();
                try {
                    transacao.executeWithoutResult(status -> aplicar(List.of(item), unitario));
                    somar(resumo, unitario);
                } catch (RuntimeException erro) {
                    resumo.erros.put(item.idExterno(), erro.getMessage());
                }
            }
        }
    }

    private void aplicar(List<ItemCatalogo> lote, Resumo resumo) {
        String origem = lote.get(0).origem();
        List<String> ids = lote.stream().map(ItemCatalogo::idExterno).toList();
        Map<String, Filme> existentes = new HashMap<>(filmeRepository.findByOrigemAndIdExternoIn(origem, ids)
                .stream().collect(Collectors.toMap(Filme::getIdExterno, Function.identity())));

        List<Filme> paraSalvar = new ArrayList<>();
        for (ItemCatalogo item : lote) {
            Filme filme = existentes.get(item.idExterno());
            if (filme != null && item.hash().equals(filme.getHashOrigem())) {
                resumo.iguais++;
                continue;
            }
            if (filme == null) {
                filme = new Filme();
                filme.setOrigem(item.origem());
                filme.setIdExterno(item.idExterno());
                resumo.criados++;
            } else {
                resumo.atualizados++;
            }
            preencher(filme, item);
            paraSalvar.add(filme);
        }
        filmeRepository.saveAll(paraSalvar);
    }

    private static void preencher(Filme filme, ItemCatalogo item) {
        filme.setTitulo(item.titulo());
        filme.setSinopse(item.sinopse());
        filme.setUrlPoster(item.urlPoster());
        filme.setGeneros(item.generos() == null ? new ArrayList<>() : new ArrayList<>(item.generos()));
        filme.setDataLancamento(item.dataLancamento() == null ? null : LocalDate.parse(item.dataLancamento()));
        filme.setDuracaoSegundos(item.duracaoSegundos() == null ? 0 : item.duracaoSegundos());
        filme.setUrl(item.urlVideo());
        filme.setClassificacaoIndicativa(item.classificacaoIndicativa());
        filme.setUf(item.uf());
        filme.setTags(item.tags());
        filme.setFichaTecnica(item.fichaTecnica() == null ? null : new LinkedHashMap<>(item.fichaTecnica()));
        filme.setStatus("INATIVA".equals(item.status()) ? StatusMidia.INATIVA : StatusMidia.ATIVA);
        filme.setHashOrigem(item.hash());
        filme.setSincronizadoEm(Instant.now());
    }

    private static void somar(Resumo total, Resumo parcial) {
        total.criados += parcial.criados;
        total.atualizados += parcial.atualizados;
        total.iguais += parcial.iguais;
        total.erros.putAll(parcial.erros);
    }
}
