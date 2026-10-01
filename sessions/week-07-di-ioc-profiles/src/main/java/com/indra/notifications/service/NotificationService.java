package com.indra.notifications.service;

import com.indra.notifications.audit.NotificationAuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationAuditLog auditLog;

    public void notify(String to, String subject, String body) {
        String env = System.getenv("APP_ENV");

        if ("prod".equals(env)) {
            System.out.println("[SMTP] Conectando a servidor real y enviando a " + to);
        } else {
            System.out.println("[FAKE] Simulando envío a " + to + ": " + subject + " -> " + body);
        }

        auditLog.record(to, subject);
    }
}
