package com.tester.stress.inbound.rest;

import com.tester.stress.scope.TestScope;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Setter
@TestScope
@Component
public class StateBean {
    private String value;
}
