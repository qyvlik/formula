package io.github.qyvlik.formula.modules.api;

import io.github.qyvlik.formula.common.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class IndexController {
    @GetMapping(value = {"/", "/index"})
    public Result<Long> index() {
        return Result.success(System.currentTimeMillis());
    }
}
