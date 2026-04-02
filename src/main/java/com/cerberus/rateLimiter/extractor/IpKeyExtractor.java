package com.cerberus.rateLimiter.extractor;

import jakarta.servlet.http.HttpServletRequest;

public class IpKeyExtractor implements KeyExtractor {
    private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";

    // good practice to add @Override anotation whenever implementing interface methods cause name changes will be
    // caught by compiler
    @Override
    public String extractRemoteAddr(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String x;

        if ((x = request.getHeader(HEADER_X_FORWARDED_FOR)) != null) {
            remoteAddr = x;
            int idx = remoteAddr.indexOf(",");
            if (idx > -1) {
                remoteAddr = remoteAddr.substring(0, idx);
            }
        }

        return remoteAddr;
    }
}