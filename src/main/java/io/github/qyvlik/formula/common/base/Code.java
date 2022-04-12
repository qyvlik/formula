package io.github.qyvlik.formula.common.base;

public interface Code {
    int SUCCESS = 200;

    int FORBIDDEN = 403;

    int NOT_FOUND = 404;

    int SYSTEM_ERROR = 500;

    int ILLEGAL_REQUEST = 501;

    int ILLEGAL_PARAM = 502;

    int EXCEED_SIZE = 503;

    int PARAM_MISSING = 504;

    int FREQUENT_INVOKE = 510;

    int DENY_LIST = 511;

    int REJECT_ANONYMOUS = 512;
}
