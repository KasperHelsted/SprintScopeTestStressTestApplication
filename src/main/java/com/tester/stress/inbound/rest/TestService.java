package com.tester.stress.inbound.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {
    private final StateBean stateBean;

    public boolean tester(String value) {
        if (!Objects.equals(value, stateBean.getValue())) {
            log.atError().log("Bean value variation!: " + value + ", " + stateBean.getValue() + ", " + (Objects.equals(value, stateBean.getValue())));
            return false;
        }
        return true;
    }
}
