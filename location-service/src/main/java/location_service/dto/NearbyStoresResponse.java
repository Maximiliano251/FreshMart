package location_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyStoresResponse {
    private String storeName;
    private int totalEncontradas;
    private List<StoreLocation> sucursales;
}