package location_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import location_service.dto.LocationRequest;
import location_service.dto.NearbyStoresResponse;
import location_service.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // buscar sucursales de una tienda específica cerca del usuario
    @PostMapping("/nearby")
    public ResponseEntity<NearbyStoresResponse> getNearby(
            @RequestBody LocationRequest request) {
        return ResponseEntity.ok(
            locationService.getNearbyStores(request)
        );
    }

    // buscar sucursales de TODAS las tiendas cerca del usuario
    @GetMapping("/nearby/all")
    public ResponseEntity<List<NearbyStoresResponse>> getAllNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radiusKm) {
        return ResponseEntity.ok(
            locationService.getAllNearbyStores(lat, lng, radiusKm)
        );
    }
}