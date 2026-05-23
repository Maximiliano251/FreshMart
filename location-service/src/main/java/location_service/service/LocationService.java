package location_service.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import location_service.dto.LocationRequest;
import location_service.dto.NearbyStoresResponse;
import location_service.dto.StoreLocation;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    @Value("${google.maps.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    // sucursales simuladas de las tiendas mock
    // cuando el mock-store-service esté listo, esto vendrá de allá
    private static final Map<String, List<StoreLocation>> SUCURSALES = Map.of(
        "simermart", List.of(
            StoreLocation.builder().nombre("SimerMart Maipú")
                .direccion("Av. Pajaritos 3261, Maipú")
                .lat(-33.5117).lng(-70.7677).horario("08:00 - 22:00").build(),
            StoreLocation.builder().nombre("SimerMart Quilicura")
                .direccion("Av. Américo Vespucio 1000, Quilicura")
                .lat(-33.3617).lng(-70.7292).horario("08:00 - 22:00").build(),
            StoreLocation.builder().nombre("SimerMart La Florida")
                .direccion("Av. Vicuña Mackenna 7110, La Florida")
                .lat(-33.5167).lng(-70.5986).horario("09:00 - 21:00").build(),
            StoreLocation.builder().nombre("SimerMart Pudahuel")
                .direccion("Av. Nueva Pudahuel 1234, Pudahuel")
                .lat(-33.4441).lng(-70.7517).horario("08:00 - 22:00").build(),
            StoreLocation.builder().nombre("SimerMart San Bernardo")
                .direccion("Av. Colón 1234, San Bernardo")
                .lat(-33.5925).lng(-70.6989).horario("09:00 - 21:00").build()
        ),
        "frescopro", List.of(
            StoreLocation.builder().nombre("FrescoPro Ñuñoa")
                .direccion("Av. Irarrázaval 1234, Ñuñoa")
                .lat(-33.4569).lng(-70.5989).horario("09:00 - 21:00").build(),
            StoreLocation.builder().nombre("FrescoPro Providencia")
                .direccion("Av. Providencia 1234, Providencia")
                .lat(-33.4317).lng(-70.6236).horario("09:00 - 22:00").build(),
            StoreLocation.builder().nombre("FrescoPro San Miguel")
                .direccion("Av. Departamental 1234, San Miguel")
                .lat(-33.4958).lng(-70.6564).horario("09:00 - 21:00").build(),
            StoreLocation.builder().nombre("FrescoPro Las Condes")
                .direccion("Av. Apoquindo 1234, Las Condes")
                .lat(-33.4105).lng(-70.5764).horario("10:00 - 22:00").build()
        ),
        "megacanasta", List.of(
            StoreLocation.builder().nombre("MegaCanasta Vitacura")
                .direccion("Av. Vitacura 1234, Vitacura")
                .lat(-33.3850).lng(-70.5934).horario("10:00 - 22:00").build(),
            StoreLocation.builder().nombre("MegaCanasta Parque Arauco")
                .direccion("Av. Kennedy 5413, Las Condes")
                .lat(-33.4033).lng(-70.5792).horario("10:00 - 22:00").build(),
            StoreLocation.builder().nombre("MegaCanasta La Reina")
                .direccion("Av. Larraín 1234, La Reina")
                .lat(-33.4481).lng(-70.5592).horario("09:00 - 21:00").build()
        )
    );

    public NearbyStoresResponse getNearbyStores(LocationRequest request) {
        String storeName = request.getStoreName().toLowerCase();
        List<StoreLocation> sucursales = SUCURSALES.getOrDefault(
            storeName, List.of()
        );

        // calcula distancia a cada sucursal y agrega el link de Maps
        List<StoreLocation> conDistancia = sucursales.stream()
            .map(s -> {
                double distancia = calcularDistanciaKm(
                    request.getLat(), request.getLng(),
                    s.getLat(), s.getLng()
                );
                return StoreLocation.builder()
                    .nombre(s.getNombre())
                    .direccion(s.getDireccion())
                    .lat(s.getLat())
                    .lng(s.getLng())
                    .horario(s.getHorario())
                    .distanciaKm(Math.round(distancia * 10.0) / 10.0)
                    .mapsUrl("https://www.google.com/maps/dir/?api=1"
                        + "&destination=" + s.getLat() + "," + s.getLng())
                    .build();
            })
            // filtra por radio y ordena por distancia
            .filter(s -> s.getDistanciaKm() <= request.getRadiusKm())
            .sorted(Comparator.comparingDouble(StoreLocation::getDistanciaKm))
            .collect(Collectors.toList());

        return NearbyStoresResponse.builder()
            .storeName(request.getStoreName())
            .totalEncontradas(conDistancia.size())
            .sucursales(conDistancia)
            .build();
    }

    public List<NearbyStoresResponse> getAllNearbyStores(
            double lat, double lng, double radiusKm) {
        return SUCURSALES.keySet().stream()
            .map(store -> {
                LocationRequest req = new LocationRequest();
                req.setLat(lat);
                req.setLng(lng);
                req.setStoreName(store);
                req.setRadiusKm(radiusKm);
                return getNearbyStores(req);
            })
            .filter(r -> !r.getSucursales().isEmpty())
            .collect(Collectors.toList());
    }

    // fórmula Haversine — calcula distancia real entre dos coordenadas GPS
    private double calcularDistanciaKm(double lat1, double lng1,
                                        double lat2, double lng2) {
        final int R = 6371; // radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1))
                 * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}