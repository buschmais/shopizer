package com.salesmanager.core.model.catalog;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PRODUCT_INFO")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
@Getter
@Setter
public class ProductInfo {

    @Id
    @NotNull
    private Long id;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MANUFACTURER_CODE")
    private String manufacturerCode;

}
