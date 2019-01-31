package com.salesmanager.core.model.catalog;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name="TAX_CLASS_ID", nullable=true)
    private TaxClass taxClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MERCHANT_ID", nullable=false)
    private MerchantStore merchantStore;

    @Embedded
    private Dimension dimension;

    @Embedded
    private AvailabilityInformation availabilityInformation;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductAttributeInfo> attributes = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductDescriptionInfo> descriptions = new HashSet<>();

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

    public ProductDescriptionInfo getProductDescription() {
        if (this.descriptions != null && !this.descriptions.isEmpty()) {
            return this.descriptions.iterator().next();
        }
        return null;
    }

}
