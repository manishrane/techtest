package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.impl.DataBodyServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.db.dataplatform.techtest.TestDataHelper.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataBodyServiceTests {

    public static final String TEST_NAME_NO_RESULT = "TestNoResult";

    @Mock
    private DataStoreRepository dataStoreRepositoryMock;

    private DataBodyService dataBodyService;
    private DataBodyEntity expectedDataBodyEntity;
    private List<DataBodyEntity> dataBodyEntityList;

    @Before
    public void setup() {
        DataHeaderEntity testDataHeaderEntity = createTestDataHeaderEntity(Instant.now());
        expectedDataBodyEntity = createTestDataBodyEntity(testDataHeaderEntity);
        dataBodyEntityList = createTestListDataBodyEntity();

        dataBodyService = new DataBodyServiceImpl(dataStoreRepositoryMock);
    }

    @Test
    public void shouldSaveDataBodyEntityAsExpected() {
        dataBodyService.saveDataBody(expectedDataBodyEntity);

        verify(dataStoreRepositoryMock, times(1))
                .save(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldGettDataByBlockTypeAsExpected() {
        when(dataStoreRepositoryMock.findDataByBlockType(BlockTypeEnum.BLOCKTYPEA)).thenReturn(dataBodyEntityList);
        dataBodyService.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);

        verify(dataStoreRepositoryMock, times(1)).findDataByBlockType(BlockTypeEnum.BLOCKTYPEA);
    }


    @Test
    public void shouldGettDataaByBlockNameAsExpected() {
        Optional<DataBodyEntity> dataBodyEntity = Optional.of(expectedDataBodyEntity);

        when(dataStoreRepositoryMock.findDataByBlockName(anyString())).thenReturn(dataBodyEntity);
        dataBodyService.getDataByBlockName("Test");

        verify(dataStoreRepositoryMock, times(1)).findDataByBlockName("Test");
    }


}
