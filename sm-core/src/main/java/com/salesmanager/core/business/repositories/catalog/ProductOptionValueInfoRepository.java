package com.salesmanager.core.business.repositories.catalog;

import com.salesmanager.core.model.catalog.ProductOptionValueInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionValueInfoRepository extends JpaRepository<ProductOptionValueInfo, Long> {

}
