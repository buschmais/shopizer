package com.salesmanager.core.model.catalog;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PRODUCT_INFO")
@NoArgsConstructor
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

    @Embedded
    private Dimension dimension;

    @Embedded
    private AvailabilityInformation availabilityInformation;

    public ProductInfo(Long id, String sku, String name, String manufacturerCode) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.manufacturerCode = manufacturerCode;
    }

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class Dimension {

        private Double width;
        private Double length;
        private Double height;
        private Double weight;

    }

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class AvailabilityInformation {

        private Boolean available;
        private Boolean shippable;
        private Boolean virtual;

    }

}
