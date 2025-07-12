/*
 * @ (#) EmailServiceImpl.java 1.0 7/11/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service.impl;

import com.benhvien1a.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/*
 * @description: Implementation of EmailService for sending password reset and verification emails
 * @author: Nguyen Truong An
 * @date: 7/11/2025
 * @version: 1.0
 */
@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendResetPasswordEmail(String to, String resetLink) {
        logger.info("Gửi email đặt lại mật khẩu tới: {}", to);
        sendEmail(to, "Đặt Lại Mật Khẩu - AaaMovies",
                "<h2>Đặt Lại Mật Khẩu</h2>" +
                        "<p>Nhấn vào liên kết dưới đây để đặt lại mật khẩu:</p>" +
                        "<a href=\"" + resetLink + "\">Đặt Lại Mật Khẩu</a>" +
                        "<p>Liên kết này sẽ hết hạn sau 1 giờ.</p>" +
                        "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>");
    }

    @Override
    public void sendVerificationEmail(String to, String code) {
        logger.info("Gửi email mã xác thực tới: {}", to);
        sendEmail(to, "Xác Thực Email - AaaMovies",
                "<h2>Xác Thực Email</h2>" +
                        "<p>Mã xác thực của bạn là: <b>" + code + "</b></p>" +
                        "<p>Mã này sẽ hết hạn sau 60 giây.</p>" +
                        "<p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>");
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Gửi email thành công tới: {}", to);
        } catch (MessagingException e) {
            logger.error("Gửi email thất bại tới {}: {}", to, e.getMessage());
            throw new RuntimeException("Gửi email thất bại", e);
        }
    }
}