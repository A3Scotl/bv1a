package com.benhvien1a.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    public static void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMissing()  // tránh lỗi khi không có file .env
                .load();

        // Ghi các biến từ file .env (nếu có) vào System Properties,
        // không ghi đè biến hệ thống đã có (từ Render)
        dotenv.entries().forEach(entry -> {
            if (System.getProperty(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }
}

