package com.cerberus.rateLimiter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/gime_tad_good_stuff")
    String getResourceAPI() {
        String resource = "good_stuff";
        return resource;
    }
}
