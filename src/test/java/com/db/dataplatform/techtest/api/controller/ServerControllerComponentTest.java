package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.BlockNotFoundAdvice;
import com.db.dataplatform.techtest.server.exception.BlockNotFoundException;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerComponentTest {

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");
    public static final String URI_PUSHBIGDATA = "http://localhost:8090/dataserver/pushbigdata";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Server serverMock;
    private DataEnvelope testDataEnvelope;
    private List<DataEnvelope> testDataEnvelopes;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private ServerController serverController;

    @Before
    public void setUp() throws HadoopClientException, NoSuchAlgorithmException, IOException {
        serverController = new ServerController(serverMock, restTemplate);

        mockMvc = standaloneSetup(serverController)
                .setControllerAdvice(new BlockNotFoundAdvice())
                .build();
        objectMapper = Jackson2ObjectMapperBuilder
                .json()
                .build();

        testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();
        testDataEnvelopes = TestDataHelper.createTestListOfDataEnvelopeApiObject();

        when(serverMock.saveDataEnvelope(any(DataEnvelope.class), anyString())).thenReturn(true);


    }

    @Test
    public void testPushDataPostCallWorksAsExpected() throws Exception {

        String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

        MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA).header("X-Checksum", "8b1a9953c4611296a827abf8c47804d7")
                .content(testDataEnvelopeJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
        assertThat(checksumPass).isTrue();
    }


    @Test
    public void testPushDataPostCallChecksumDoesNotMatch() throws Exception {
        when(serverMock.saveDataEnvelope(any(DataEnvelope.class), eq("8b1a9953c4611296a827abf8c47804"))).thenReturn(false);

        String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

        MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA).header("X-Checksum", "8b1a9953c4611296a827abf8c47804")
                .content(testDataEnvelopeJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
        assertThat(checksumPass).isFalse();
    }


    @Test
    public void testGetDataForExitingBlockType() throws Exception {
        when(serverMock.getData(any(BlockTypeEnum.class))).thenReturn(testDataEnvelopes);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("blockType", "BLOCKTYPEA");

        mockMvc.perform(get(URI_GETDATA.expand(uriVariables)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].dataHeader.name").value(testDataEnvelope.getDataHeader().getName()))
                .andExpect(jsonPath("$[0].dataBody.dataBody").value(testDataEnvelope.getDataBody().getDataBody()));


    }

    @Test
    public void testGetDataForNoBlockTypeIsNotFound() throws Exception {
        when(serverMock.getData(any(BlockTypeEnum.class))).thenReturn(new ArrayList<DataEnvelope>());

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("blockType", "BLOCKTYPEA");

        mockMvc.perform(get(URI_GETDATA.expand(uriVariables)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    public void testGetDataForBlockTypeBadRequest() throws Exception {
        when(serverMock.getData(any(BlockTypeEnum.class))).thenReturn(new ArrayList<DataEnvelope>());

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("blockType", "BLOCKTYPE");

        mockMvc.perform(get(URI_GETDATA.expand(uriVariables)))
                .andExpect(status().isBadRequest());

    }


    @Test
    public void testUpdateDataWithNewBlockType() throws Exception {
        when(serverMock.updateDataByBlockName(anyString(), any(BlockTypeEnum.class))).thenReturn(true);
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("newBlockType", "BLOCKTYPEB");
        uriVariables.put("name", "Test");

        MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand(uriVariables)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
        assertThat(checksumPass).isTrue();


    }

    @Test
    public void testUpdateDataWhenNameDoesNotExist() throws Exception {
        when(serverMock.updateDataByBlockName(anyString(), any(BlockTypeEnum.class))).thenReturn(true);
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("newBlockType", "BLOCKTYPE");
        uriVariables.put("name", "Test");

        mockMvc.perform(patch(URI_PATCHDATA.expand(uriVariables)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateDataForWrongBlockType() throws Exception {
        when(serverMock.updateDataByBlockName(anyString(), any(BlockTypeEnum.class))).thenThrow(new BlockNotFoundException("No block found with the Name :Test"));
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("newBlockType", "BLOCKTYPE");
        uriVariables.put("name", "Test");

        mockMvc.perform(patch(URI_PATCHDATA.expand(uriVariables)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPushBigDataPostCallWorksAsExpected() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        Map<String, String> uriVariables = new HashMap<>();

        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class)))
                .thenReturn(responseEntity);

        MvcResult mvcResult = mockMvc.perform(post(URI_PUSHBIGDATA)
                .content(TestDataHelper.DUMMY_PAYLOAD)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

    }


    @Test
    public void testPushBigDataPostCallGatewayTimeOut() throws Exception {
        ResponseEntity re = new ResponseEntity(HttpStatus.GATEWAY_TIMEOUT);

        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.GATEWAY_TIMEOUT));

        mockMvc.perform(post(URI_PUSHBIGDATA)
                .content(TestDataHelper.DUMMY_PAYLOAD)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isGatewayTimeout());


    }


}
