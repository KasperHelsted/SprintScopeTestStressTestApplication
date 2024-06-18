package com.tester.stress.inbound.rest;

import com.tester.stress.inbound.rest.dto.StatusObjectDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InboundRest implements StatusApiDelegate {
    private final TestService testService;
    private final StateBean stateBean;

    @Override
    public ResponseEntity<StatusObjectDTO> getQueueDepth(String testParam) {
        stateBean.setValue(testParam);
        boolean resp = testService.tester(testParam);

        if (resp) {
            return ResponseEntity.ok(null);
        }
        return (ResponseEntity<StatusObjectDTO>) ResponseEntity.badRequest();

    }
}
