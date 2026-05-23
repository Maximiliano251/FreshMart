package location_service.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private double lat;          // latitud del usuario
    private double lng;          // longitud del usuario
    private String storeName;    // "SimerMart", "FrescoPro", "MegaCanasta"
    private double radiusKm;     // radio de búsqueda en km
}