package location_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreLocation {
    private String nombre;
    private String direccion;
    private double lat;
    private double lng;
    private double distanciaKm;
    private String horario;
    private String mapsUrl;      // link directo a Google Maps
}