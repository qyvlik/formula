package io.github.qyvlik.formula.modules.api.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeleteVariablesReq {
    private List<String> variableNames;
}
