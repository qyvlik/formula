package io.github.qyvlik.formula.common.interceptor;

import com.alibaba.fastjson.JSON;
import io.github.qyvlik.formula.common.base.Code;
import io.github.qyvlik.formula.common.base.Result;
import io.github.qyvlik.formula.common.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
public class OpenAccessInterceptor implements HandlerInterceptor {

    @Value("#{${auth.tokens}}")
    private Map<String, String> authTokens;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             Object o) throws Exception {

        String accessToken = httpServletRequest.getHeader("token");

        if (StringUtils.isNotBlank(accessToken)) {
            return true;
        }

        String uri = httpServletRequest.getRequestURI();

        ServletUtils.writeJsonString(httpServletResponse,
                JSON.toJSONString(Result.failure(Code.FORBIDDEN, uri + " need `token` header")));

        return false;
    }
}
