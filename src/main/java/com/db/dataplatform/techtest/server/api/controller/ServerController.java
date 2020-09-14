package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;
    @Autowired
    private final RestTemplate restTemplate;
    @Value("${hadoop.server.url}")
    String HADOOP_SERVER_URL;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(@RequestBody DataEnvelope dataEnvelope, @RequestHeader(name = "X-Checksum", required = false) String checksum) throws IOException, NoSuchAlgorithmException {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
        boolean checksumPass = server.saveDataEnvelope(dataEnvelope, checksum);

        log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        return ResponseEntity.ok(checksumPass);
    }

    @GetMapping(value = "/data/{blockType}")
    public ResponseEntity<List<DataEnvelope>> getData(@PathVariable BlockTypeEnum blockType) {

        log.info("Request envelope received for getData: {}", blockType);
        List<DataEnvelope> envelopes = server.getData(blockType);
        return new ResponseEntity<List<DataEnvelope>>(envelopes, HttpStatus.OK);
    }


    @PatchMapping(value = "/update/{blockName}/{blockType}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateData(@Valid @PathVariable String blockName, @PathVariable BlockTypeEnum blockType) {
        log.info("Update Request received for ", blockName);
        boolean updated = server.updateDataByBlockName(blockName, blockType);
        log.info("Updated record ", blockName);
        return ResponseEntity.ok(updated);

    }

    @PostMapping(value = "/pushbigdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> pushBigData(@RequestBody String payload) throws IOException, NoSuchAlgorithmException {

        log.info("Payload received: {}", payload);
        ResponseEntity<HttpStatus> status = null;
        try {

            HttpEntity httpEntity = new HttpEntity(payload);
            status = restTemplate.exchange(HADOOP_SERVER_URL, HttpMethod.POST, httpEntity, HttpStatus.class);

        } catch (HttpServerErrorException.GatewayTimeout e) {
            log.error("Time out exception: ", e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();


        }
        return status;
    }


}
