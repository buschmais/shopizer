package com.salesmanager.core.business.repositories.catalog;

import com.salesmanager.core.model.catalog.ProductOptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionInfoRepository extends JpaRepository<ProductOptionInfo, Long> {

}

