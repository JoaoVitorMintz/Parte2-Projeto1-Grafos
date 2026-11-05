import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servidor HTTP mínimo para ligar o HTML visual ao TGrafo do projeto.
 * Usa o Haversine e o Dijkstra existentes em TGrafo, sem duplicar esses algoritmos.
 */
public class VisualizacaoServidor {
    private static final int PORTA = 8080;
    private static final double RAIO_CONEXAO_KM = 5.0;
    private static final Pattern LINHA_ABRIGO = Pattern.compile("^\\s*(\\d+)\\s+\"([^\"]*)\"\\s+\"([^\"]*)\"\\s*$");
    private static final String ARQUIVO_DADOS = "geocodificador/centro_acolhida_adultos_geocodificado.txt";
    private static final String ARQUIVO_HTML = "algoritmos-visual.html";

    private final Path raiz;

    private VisualizacaoServidor(Path raiz) {
        this.raiz = raiz;
    }

    public static void main(String[] args) throws IOException {
        Path raiz = localizarRaiz();
        if (!Files.isRegularFile(raiz.resolve(ARQUIVO_HTML))) {
            throw new IOException("Não encontrei " + ARQUIVO_HTML + " em " + raiz.toAbsolutePath());
        }
        if (!Files.isRegularFile(raiz.resolve(ARQUIVO_DADOS))) {
            throw new IOException("Não encontrei " + ARQUIVO_DADOS + " em " + raiz.toAbsolutePath());
        }

        int porta = PORTA;
        if (args.length > 0) {
            try {
                porta = Integer.parseInt(args[0]);
                if (porta < 1 || porta > 65535) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Informe uma porta válida entre 1 e 65535.");
            }
        }
        HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        VisualizacaoServidor app = new VisualizacaoServidor(raiz);
        servidor.createContext("/api/local/grafo", app::servirGrafo);
        servidor.createContext("/", app::servirPagina);
        servidor.start();
        System.out.println("Visualização do AcolheSP iniciada.");
        System.out.println("Abra: http://localhost:" + porta + "/algoritmos-visual.html");
        System.out.println("Dados: " + raiz.resolve(ARQUIVO_DADOS).toAbsolutePath());
        System.out.println("Pressione Ctrl+C para encerrar o servidor.");
    }

    private static Path localizarRaiz() {
        Path atual = Paths.get(".").toAbsolutePath().normalize();
        if (Files.isRegularFile(atual.resolve(ARQUIVO_DADOS))) return atual;
        Path projetoDentro = atual.resolve("Parte2-Projeto1-Grafos-main");
        if (Files.isRegularFile(projetoDentro.resolve(ARQUIVO_DADOS))) return projetoDentro;
        return atual;
    }

    private void servirPagina(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            responderTexto(exchange, 405, "Método não permitido.");
            return;
        }
        String caminho = exchange.getRequestURI().getPath();
        if (!"/".equals(caminho) && !("/" + ARQUIVO_HTML).equals(caminho)) {
            responderTexto(exchange, 404, "Página não encontrada.");
            return;
        }
        byte[] html = Files.readAllBytes(raiz.resolve(ARQUIVO_HTML));
        responder(exchange, 200, "text/html; charset=utf-8", html);
    }

    private void servirGrafo(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            responderJson(exchange, 405, "{\"erro\":\"Método não permitido\"}");
            return;
        }
        try {
            Map<String, String> query = lerQuery(exchange.getRequestURI().getRawQuery());
            double latitude = Double.parseDouble(query.getOrDefault("lat", ""));
            double longitude = Double.parseDouble(query.getOrDefault("lng", ""));
            if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                    || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
                responderJson(exchange, 400, "{\"erro\":\"Latitude/longitude fora do intervalo válido\"}");
                return;
            }
            String json = construirResposta(latitude, longitude);
            responderJson(exchange, 200, json);
        } catch (NumberFormatException e) {
            responderJson(exchange, 400, "{\"erro\":\"Informe lat e lng numéricos\"}");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            responderJson(exchange, 500, "{\"erro\":" + jsonString("Falha ao construir o grafo: " + e.getMessage()) + "}");
        }
    }

    private String construirResposta(double latitude, double longitude) throws IOException {
        List<Abrigo> abrigos = lerAbrigos(raiz.resolve(ARQUIVO_DADOS));
        TGrafo grafo = new TGrafo(0);
        List<VerticeResposta> vertices = new ArrayList<>();

        // O primeiro vértice é sempre a origem, índice 0 para o Dijkstra já existente.
        grafo.insereV(formatCoord(latitude) + " " + formatCoord(longitude), "Sua localização");
        vertices.add(new VerticeResposta("origem", "Sua localização", true, latitude, longitude));

        int semCoordenadas = 0;
        for (Abrigo abrigo : abrigos) {
            if (!abrigo.temCoordenadas) {
                semCoordenadas++;
                continue;
            }
            grafo.insereV(formatCoord(abrigo.latitude) + " " + formatCoord(abrigo.longitude), abrigo.nome);
            vertices.add(new VerticeResposta("abrigo-" + abrigo.id, abrigo.nome, false,
                    abrigo.latitude, abrigo.longitude));
        }

        TGrafo.ResultadoDijkstra caminhos = grafo.calcularDijkstra(0);
        ColoracaoGrafo.Resultado coloracao = ColoracaoGrafo.colorir(grafo);
        List<TGrafo.ArestaInfo> arestas = grafo.listarArestas();

        StringBuilder json = new StringBuilder(64_000);
        json.append('{');
        json.append("\"vertices\":[");
        for (int i = 0; i < vertices.size(); i++) {
            if (i > 0) json.append(',');
            VerticeResposta v = vertices.get(i);
            json.append('{');
            campo(json, "id", v.id).append(',');
            campo(json, "nome", v.nome).append(',');
            json.append("\"origem\":").append(v.origem).append(',');
            json.append("\"latitude\":").append(Double.toString(v.latitude)).append(',');
            json.append("\"longitude\":").append(Double.toString(v.longitude)).append(',');
            double distancia = caminhos.distancias[i];
            json.append("\"distanciaKm\":");
            if (Double.isFinite(distancia)) json.append(Double.toString(distancia)); else json.append("null");
            json.append(',').append("\"anteriorId\":");
            int anterior = caminhos.antecessores[i];
            if (anterior >= 0 && anterior < vertices.size()) json.append(jsonString(vertices.get(anterior).id));
            else json.append("null");
            json.append(',').append("\"cor\":").append(coloracao.cores[i]);
            json.append('}');
        }
        json.append("],\"arestas\":[");
        for (int i = 0; i < arestas.size(); i++) {
            if (i > 0) json.append(',');
            TGrafo.ArestaInfo a = arestas.get(i);
            json.append('{');
            campo(json, "origemId", vertices.get(a.origem).id).append(',');
            campo(json, "destinoId", vertices.get(a.destino).id).append(',');
            json.append("\"distanciaKm\":").append(Float.toString(a.peso));
            json.append('}');
        }
        json.append("],\"gruposColoracao\":[");
        boolean primeiroGrupo = true;
        for (Map.Entry<Integer, List<Integer>> grupo : coloracao.grupos.entrySet()) {
            if (!primeiroGrupo) json.append(',');
            primeiroGrupo = false;
            json.append("{\"cor\":").append(grupo.getKey()).append(",\"vertices\":[");
            for (int j = 0; j < grupo.getValue().size(); j++) {
                if (j > 0) json.append(',');
                json.append(jsonString(vertices.get(grupo.getValue().get(j)).id));
            }
            json.append("]}");
        }
        json.append("],\"quantidadeCores\":").append(coloracao.quantidadeCores).append(',');
        json.append("\"raioConexaoKm\":").append(RAIO_CONEXAO_KM).append(',');
        String descricao = "Grafo não direcionado; aresta criada quando a distância Haversine é de até "
                + formatCoord(RAIO_CONEXAO_KM) + " km. Dijkstra e pesos são calculados pelo TGrafo existente; "
                + "a coloração gulosa é calculada por ColoracaoGrafo. Distâncias são em linha reta, não por ruas."
                + (semCoordenadas > 0 ? " " + semCoordenadas + " abrigo(s) sem coordenadas válidas foram omitidos do grafo." : "");
        campo(json, "descricao", descricao);
        json.append('}');
        return json.toString();
    }

    private static List<Abrigo> lerAbrigos(Path arquivo) throws IOException {
        List<Abrigo> abrigos = new ArrayList<>();
        int numeroLinha = 0;
        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            numeroLinha++;
            if (linha.trim().isEmpty()) continue;
            Matcher m = LINHA_ABRIGO.matcher(linha);
            if (!m.matches()) {
                throw new IOException("Formato inesperado na linha " + numeroLinha + " de " + arquivo.getFileName());
            }
            int id = Integer.parseInt(m.group(1));
            String nome = m.group(2).trim();
            String coordenadas = m.group(3).trim();
            Double lat = null, lng = null;
            if (!coordenadas.isEmpty()) {
                String[] partes = coordenadas.split("\\s+");
                if (partes.length == 2) {
                    try {
                        lat = Double.parseDouble(partes[0]);
                        lng = Double.parseDouble(partes[1]);
                    } catch (NumberFormatException ignored) {
                        lat = null;
                        lng = null;
                    }
                }
            }
            boolean valido = lat != null && lng != null && Double.isFinite(lat) && Double.isFinite(lng)
                    && lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
            abrigos.add(new Abrigo(id, nome, valido, valido ? lat : 0, valido ? lng : 0));
        }
        return abrigos;
    }

    private static Map<String, String> lerQuery(String raw) {
        Map<String, String> query = new LinkedHashMap<>();
        if (raw == null || raw.isEmpty()) return query;
        for (String par : raw.split("&")) {
            String[] partes = par.split("=", 2);
            String chave = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
            String valor = partes.length > 1 ? URLDecoder.decode(partes[1], StandardCharsets.UTF_8) : "";
            query.put(chave, valor);
        }
        return query;
    }

    private static StringBuilder campo(StringBuilder sb, String nome, String valor) {
        return sb.append(jsonString(nome)).append(':').append(jsonString(valor));
    }

    private static String jsonString(String valor) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    private static String formatCoord(double n) {
        return Double.toString(n);
    }

    private static void responderTexto(HttpExchange ex, int status, String texto) throws IOException {
        responder(ex, status, "text/plain; charset=utf-8", texto.getBytes(StandardCharsets.UTF_8));
    }

    private static void responderJson(HttpExchange ex, int status, String json) throws IOException {
        responder(ex, status, "application/json; charset=utf-8", json.getBytes(StandardCharsets.UTF_8));
    }

    private static void responder(HttpExchange ex, int status, String contentType, byte[] corpo) throws IOException {
        Headers headers = ex.getResponseHeaders();
        headers.set("Content-Type", contentType);
        headers.set("Cache-Control", "no-store");
        headers.set("X-Content-Type-Options", "nosniff");
        ex.sendResponseHeaders(status, corpo.length);
        try (var os = ex.getResponseBody()) {
            os.write(corpo);
        }
    }

    private static final class Abrigo {
        final int id;
        final String nome;
        final boolean temCoordenadas;
        final double latitude;
        final double longitude;

        Abrigo(int id, String nome, boolean temCoordenadas, double latitude, double longitude) {
            this.id = id;
            this.nome = nome;
            this.temCoordenadas = temCoordenadas;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private static final class VerticeResposta {
        final String id;
        final String nome;
        final boolean origem;
        final double latitude;
        final double longitude;

        VerticeResposta(String id, String nome, boolean origem, double latitude, double longitude) {
            this.id = id;
            this.nome = nome;
            this.origem = origem;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
