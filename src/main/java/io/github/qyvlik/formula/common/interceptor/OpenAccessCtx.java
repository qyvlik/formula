package io.github.qyvlik.formula.common.interceptor;

import lombok.Data;

@Data
public class OpenAccessCtx {
    private static final ThreadLocal<OpenAccessCtx> local = ThreadLocal.withInitial(OpenAccessCtx::new);

    private String token;

    private String app;

    private String uri;

    private String ip;

    public static OpenAccessCtx peek() {
        return null;
    }

    public static OpenAccessCtx get() {
        return local.get();
    }

    public static void remove() {
        local.remove();
    }
}
