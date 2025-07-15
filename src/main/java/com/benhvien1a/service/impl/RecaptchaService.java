package com.benhvien1a.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecaptchaService {

    private static final Logger logger = LoggerFactory.getLogger(RecaptchaService.class);

    @Value("${recaptcha.secret}")
    private String secretKey;

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verify(String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", token);

            HttpEntity<MultiValueMap<String, String>> request =
                    new HttpEntity<>(params, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(VERIFY_URL, request, Map.class);

            Map<String, Object> body = response.getBody();

            logger.debug("🔍 Google reCAPTCHA raw response: {}", body);

            boolean success = body != null && Boolean.TRUE.equals(body.get("success"));

            if (!success) {
                logger.warn("❌ reCAPTCHA verification failed. Error: {}", body.get("error-codes"));
            }

            return success;
        } catch (Exception e) {
            logger.error("Lỗi khi xác thực reCAPTCHA", e);
            return false;
        }
    }
}
