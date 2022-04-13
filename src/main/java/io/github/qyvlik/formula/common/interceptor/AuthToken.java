package io.github.qyvlik.formula.common.interceptor;

import lombok.Data;

@Data
public class AuthToken {
    private String app;

    private String token;
}
