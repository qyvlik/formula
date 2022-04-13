package io.github.qyvlik.formula.common.web.ctx;

import lombok.Data;

@Data
public class AuthToken {
    private String app;

    private String token;
}
