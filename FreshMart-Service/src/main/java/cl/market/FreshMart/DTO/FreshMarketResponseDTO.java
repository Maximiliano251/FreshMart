package cl.market.FreshMart.DTO;

public class FreshMarketResponseDTO {
    public record ProductResponseDTO(Long id, String name, Double price, Integer stock) {}

}
