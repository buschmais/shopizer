package com.salesmanager.core.model.catalog;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCT_DESCRIPTION_INFO")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
@Getter
@Setter
public class ProductDescriptionInfo {

    @Id
    private Long id;

    @Column(name = "SEF_URL")
    private String seUrl;

    @Column(name="NAME", nullable = false, length=120)
    private String name;

    @JoinColumn(name = "LANGUAGE_ID")
    private Long languageId;
}
