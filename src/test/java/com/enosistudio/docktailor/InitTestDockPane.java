package com.enosistudio.docktailor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class InitTestDockPane {
    @Test
    public void additionShouldWork() {
        log.info("Starting test additionShouldWork()");
        int result = 2 + 3;
        assertEquals(5, result);

    }
}
