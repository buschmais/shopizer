package com.salesmanager.core.business.repositories.catalog;

import com.salesmanager.core.model.catalog.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInfoRepository extends JpaRepository<ProductInfo, Long> {

    @Query("SELECT p FROM ProductInfo p WHERE p.taxClass.id = ?1")
    List<ProductInfo> listByTaxClass(Long taxClassId);

}
