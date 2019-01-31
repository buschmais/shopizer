package com.salesmanager.core.model.catalog;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

}
