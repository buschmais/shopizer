package com.salesmanager.core.model.catalog;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductImageInfo {

    private Long id;
    private String imageName;
    private boolean defaultImage;
    private String imageUrl;

}
