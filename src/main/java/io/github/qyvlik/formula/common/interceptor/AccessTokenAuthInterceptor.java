package io.github.qyvlik.formula.common.interceptor;

import com.alibaba.fastjson.JSON;
import io.github.qyvlik.formula.common.base.ResponseObject;
import io.github.qyvlik.formula.common.properties.FormulaProperties;
import io.github.qyvlik.formula.common.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AccessTokenAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private FormulaProperties formulaProperties;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             Object o) throws Exception {

        String accessToken = httpServletRequest.getHeader("token");

        if (StringUtils.isNotBlank(accessToken) && formulaProperties.getAccessTokens().contains(accessToken)) {
            return true;
        }

        String uri = httpServletRequest.getRequestURI();

        ResponseObject<String> responseObject =
                new ResponseObject<>(400, uri + " need `token` header");
        ServletUtils.writeJsonString(httpServletResponse, JSON.toJSONString(responseObject));

        return false;
    }
}
