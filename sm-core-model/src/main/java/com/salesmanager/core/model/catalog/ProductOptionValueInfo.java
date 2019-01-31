package com.salesmanager.core.model.catalog;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="PRODUCT_OPTION_VALUE_INFO")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
@Getter
@Setter
public class ProductOptionValueInfo {

    @Id
    @Column(name="PRODUCT_OPTION_VALUE_ID")
    private Long id;

    @Column(name="PRODUCT_OPTION_VAL_CODE")
    private String code;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ProductOptionValueDescriptionInfo> descriptions = new HashSet<>();

}
