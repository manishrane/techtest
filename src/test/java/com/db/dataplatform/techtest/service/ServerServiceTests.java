package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.exception.BlockNotFoundException;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import static com.db.dataplatform.techtest.TestDataHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;

    private ModelMapper modelMapper;

    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;
    private List<DataEnvelope> testDataEnvelopes;
    private List<DataBodyEntity> testDataBodyEntities;

    private Server server;

    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        server = new ServerImpl(dataBodyServiceImplMock, modelMapper);

        testDataEnvelopes = createTestListOfDataEnvelopeApiObject();
        testDataBodyEntities = createTestListDataBodyEntity();
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        boolean success = server.saveDataEnvelope(testDataEnvelope, "cecfd3953783df706878aaec2c22aa70");

        assertThat(success).isTrue();
//        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }


    @Test
    public void testgetDataForSizeThree() throws Exception {

        when(dataBodyServiceImplMock.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA)).thenReturn(testDataBodyEntities);
        List<DataEnvelope> dataEnvelopeList = server.getData(BlockTypeEnum.BLOCKTYPEA);
        assertThat(dataEnvelopeList.size()==3).isTrue();



    }

    @Test
    public void testUpdateDataByBlockName() throws Exception {
        Optional<DataBodyEntity> dataBodyEntity = Optional.of(expectedDataBodyEntity);
        when(dataBodyServiceImplMock.getDataByBlockName(anyString())).thenReturn(dataBodyEntity);
        boolean updated = server.updateDataByBlockName("Test", BlockTypeEnum.BLOCKTYPEB);
        assertThat(updated).isTrue();


    }


    @Test(expected = BlockNotFoundException.class)
    public void testUpdateDataThrowBlockNotFound() throws Exception {
        Optional<DataBodyEntity> dataBodyEntity = Optional.of(expectedDataBodyEntity);
        when(dataBodyServiceImplMock.getDataByBlockName(anyString())).thenThrow(new BlockNotFoundException(""));


        boolean updated = server.updateDataByBlockName("Test", BlockTypeEnum.BLOCKTYPEB);


    }
}
