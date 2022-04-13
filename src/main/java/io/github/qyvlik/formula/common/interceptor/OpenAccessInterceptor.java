package io.github.qyvlik.formula.common.interceptor;

import com.alibaba.fastjson.JSON;
import io.github.qyvlik.formula.common.base.Code;
import io.github.qyvlik.formula.common.base.Result;
import io.github.qyvlik.formula.common.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Service
public class OpenAccessInterceptor implements HandlerInterceptor {

    @Value("#{${auth.tokens}}")
    private Map<String, String> tokens;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        final String token = httpServletRequest.getHeader("token");
        final String uri = httpServletRequest.getRequestURI();

        AuthToken authToken = getAuthTokenByToken(token);

        if (StringUtils.isNotBlank(token) && authToken != null) {
            OpenAccessCtx.get().setApp(authToken.getApp());
            OpenAccessCtx.get().setToken(authToken.getToken());
            OpenAccessCtx.get().setIp(ServletUtils.getIp());
            OpenAccessCtx.get().setUri(uri);
            return true;
        }

        ServletUtils.writeJsonString(httpServletResponse,
                JSON.toJSONString(Result.failure(Code.FORBIDDEN, uri + " need `token` header")));

        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        OpenAccessCtx.remove();
    }

    private AuthToken getAuthTokenByToken(String token) {
        for (Map.Entry<String, String> entry : tokens.entrySet()) {
            if (entry.getValue().equals(token)) {
                AuthToken authToken = new AuthToken();
                authToken.setApp(entry.getKey());
                authToken.setToken(entry.getValue());
                return authToken;
            }
        }
        return null;
    }
}
