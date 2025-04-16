package com.reservemate.reserve_mate_backend.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Value("${spring.mail.smtp.host}")
    private String host;
    @Value("${spring.mail.smtp.username}")
    private String userName;
    @Value("${spring.mail.smtp.password}")
    private String password;
    @Value("${spring.mail.smtp.port}")
    private int port;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setUsername(userName);
        javaMailSender.setPassword(password);
        javaMailSender.setPort(port);
        javaMailSender.setJavaMailProperties(getMailProperties());
        javaMailSender.setDefaultEncoding("UTF-8");

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");    //tls사용
        properties.setProperty("mail.smtp.ssl.enable", "false");
        properties.setProperty("mail.smtp.ssl.trust", host);
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

        return properties;

    }

}
