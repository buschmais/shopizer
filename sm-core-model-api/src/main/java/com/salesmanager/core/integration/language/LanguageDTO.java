package com.salesmanager.core.integration.language;

import com.salesmanager.core.integration.AbstractCoreCrudDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LanguageDTO extends AbstractCoreCrudDTO {

    private Integer id;

    private String code;

}

