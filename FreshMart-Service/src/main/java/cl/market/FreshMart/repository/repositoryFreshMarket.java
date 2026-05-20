package cl.market.FreshMart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.market.FreshMart.DTO.FreshMarketResponseDTO;

@Repository
public interface repositoryFreshMarket extends JpaRepository<FreshMarketResponseDTO, Long> {

}
