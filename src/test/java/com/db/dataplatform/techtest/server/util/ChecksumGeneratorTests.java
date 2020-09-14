package com.db.dataplatform.techtest.server.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChecksumGeneratorTests {

    @Test
    void getCheckSum() {

        assertEquals("cecfd3953783df706878aaec2c22aa70", ChecksumGenerator.getCheckSum("AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D"));
    }
}