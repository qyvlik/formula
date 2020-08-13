package io.github.qyvlik.formula.modules.api.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeleteVariablesRequest {
    private List<String> variableNames;
}
