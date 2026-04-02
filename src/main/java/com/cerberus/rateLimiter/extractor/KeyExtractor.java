package com.cerberus.rateLimiter.extractor; // package declarations must be first, imports placed above this isn't allowed
import jakarta.servlet.http.HttpServletRequest;
// we are implementing this as an interface cause ip from the header, api key from the auth header, id from the jwt
// token these three are the different implementations of the same contract - take request, return string

// HttpServletRequest is an object representing incomming http request, When a client hits your API, the servlet
// container (Tomcat, which Spring Boot embeds) parses the raw HTTP bytes and packages everything into this object
// — headers, IP, method, body, params, etc

public interface KeyExtractor {
    String extractRemoteAddr(HttpServletRequest request);
}
