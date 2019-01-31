package com.salesmanager.core.model.catalog;

import com.salesmanager.core.model.reference.language.Language;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="PRODUCT_OPTION_DESC_INFO")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
@Getter
@Setter
public class ProductOptionDescriptionInfo {

    @Id
    @Column(name = "DESCRIPTION_ID")
    private Long id;

    @Column(name="NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "LANGUAGE_ID")
    private Language language;

}
