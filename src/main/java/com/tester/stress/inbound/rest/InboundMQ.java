package com.tester.stress.inbound.rest;

import com.tester.stress.scope.CustomScope;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InboundMQ {
    private final TestService testService;
    private final CustomScope customScope;
    private final StateBean stateBean;

    //@JmsListener(destination = "TESTER", concurrency = "10-10")
    public void getJob(TextMessage message) throws JMSException {
        try {
            customScope.startJmsScope();

            stateBean.setValue(message.getText());
            testService.tester(message.getText());
        } catch (JMSException e) {
            throw e;
        } finally {
            customScope.endJmsScope();
        }
    }
}
