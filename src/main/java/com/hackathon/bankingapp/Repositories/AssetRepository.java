package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Asset findByAssetSymbol(String assetSymbol);

    Asset findByAssetSymbolAndIdentifier(String assetSymbol, String identifier);

    Asset[] findAllByIdentifier(String identifier);

    List<Asset> findByIdentifier(String identifier);
}
