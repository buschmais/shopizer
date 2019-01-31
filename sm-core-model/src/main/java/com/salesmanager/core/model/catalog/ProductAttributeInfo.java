package com.salesmanager.core.model.catalog;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCT_ATTRIBUTE_INFO")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
@Getter @Setter
public class ProductAttributeInfo {

    @Id
    private Long id;

    @Column(name="PRODUCT_ATRIBUTE_PRICE")
    private Double price;

    @Column(name="PRODUCT_ATTRIBUTE_FREE")
    private Boolean free;

    @Column(name="PRODUCT_ATTRIBUTE_WEIGHT")
    private Double weight;

    @ManyToOne
    @JoinColumn(name="OPTION_ID", nullable=false)
    private ProductOptionInfo productOption;

    @ManyToOne
    @JoinColumn(name="OPTION_VALUE_ID", nullable=false)
    private ProductOptionValueInfo productOptionValue;

}
