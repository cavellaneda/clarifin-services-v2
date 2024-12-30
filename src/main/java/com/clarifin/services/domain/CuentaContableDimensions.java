package com.clarifin.services.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CuentaContableDimensions {

    private String transactional;
    private String code;
    private String description;
    private Double initialBalance;
    private Double debits;
    private Double credits;
    private Double finalBalance;
}
