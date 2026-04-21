package de.dhbwka.navigation.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import de.dhbwka.navigation.extension.ExtensionEdgeCategory;
import de.dhbwka.navigation.graph.Graph;
import de.dhbwka.navigation.graph.edge.CategorizedGeoEdge;
import de.dhbwka.navigation.graph.edge.CategorizedWidthedGeoEdge;
import de.dhbwka.navigation.graph.node.GeoNode;
import de.dhbwka.navigation.services.NavigationService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationServer {

    private final NavigationService service;

    public NavigationServer(Graph<GeoNode, CategorizedWidthedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> graph) {
        this.service = new NavigationService(graph);
    }

    public void start() throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/nav", this::handleNavigationRequest);

        server.start();
        System.out.println("Server läuft auf http://localhost:" + port + "/nav");
    }

    private void handleNavigationRequest(HttpExchange exchange) throws IOException {
        System.out.println("Handling Request");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "http://localhost:4200");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI());
        String coords = queryParams.getOrDefault("coords", "[21533398,154916677]");
        String[] parts = coords.replace("[", "").replace("]", "").split(",");
        String start = parts[0];
        String end = parts[1];


        List<CategorizedGeoEdge<? extends GeoNode, ExtensionEdgeCategory>> path = service.calculateRoute(start, end);
        String responseJson = String.format("""
                %s
                """, path.stream().map(n -> "[" + n.getOrigin().getLongitude() + ", " + n.getOrigin().getLatitude() + "]").toList()); //TODO
        byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private static Map<String, String> parseQueryParams(URI uri) {
        Map<String, String> params = new HashMap<>();
        String query = uri.getRawQuery();
        if (query == null || query.isEmpty()) return params;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }


}
