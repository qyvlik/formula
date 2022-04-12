package io.github.qyvlik.formula.modules.api.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CalculateFormulaReq {

    @NotBlank
    @NotNull
    @Size(max = 1024)
    private String formula;
}
