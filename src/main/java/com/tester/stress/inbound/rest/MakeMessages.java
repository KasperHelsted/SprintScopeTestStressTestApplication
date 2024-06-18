package com.tester.stress.inbound.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class MakeMessages {
    private final JmsTemplate jmsTemplate;
    private final Executor taskExecutor;

    //@EventListener(ApplicationReadyEvent.class)
    public void makeMessages() {
        for (int i = 0; i < 10000000; i++) {
            taskExecutor.execute(() -> {
                jmsTemplate.convertAndSend("TESTER", UUID.randomUUID().toString());
            });
        }
    }
}
