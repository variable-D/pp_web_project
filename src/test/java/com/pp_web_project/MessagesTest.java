package com.pp_web_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessagesTest implements CommandLineRunner {

    @Autowired
    private MessageSource messageSource;

    @Override
    public void run(String... args) {
        Locale localeKo = Locale.KOREA;
        Locale localeEn = Locale.ENGLISH;

        String logoKo = messageSource.getMessage("messages.logo", null, localeKo);
        String logoEn = messageSource.getMessage("messages.logo", null, localeEn);

        System.out.println("✅ Korean logo: " + logoKo);
        System.out.println("✅ English logo: " + logoEn);
    }
}