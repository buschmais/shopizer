package com.salesmanager.core.business.repositories.catalog;

import com.salesmanager.core.model.catalog.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInfoRepository extends JpaRepository<ProductInfo, Long> {
}
