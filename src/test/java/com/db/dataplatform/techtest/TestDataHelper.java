package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestDataHelper {

    public static final String TEST_NAME = "Test";
    public static final String TEST_NAME_EMPTY = "";
    public static final String DUMMY_DATA = "AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D";
    public static final String DUMMY_PAYLOAD = "trFGtyG678rtrtgdvmakcu217t276492bkjho8yanKHKHK9797bnhhhb669HKHK00";

    public static DataHeaderEntity createTestDataHeaderEntity(Instant expectedTimestamp) {
        DataHeaderEntity dataHeaderEntity = new DataHeaderEntity();
        dataHeaderEntity.setName(TEST_NAME);
        dataHeaderEntity.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity.setCreatedTimestamp(expectedTimestamp);
        return dataHeaderEntity;
    }

    public static DataBodyEntity createTestDataBodyEntity(DataHeaderEntity dataHeaderEntity) {
        DataBodyEntity dataBodyEntity = new DataBodyEntity();
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        dataBodyEntity.setDataBody(DUMMY_DATA);
        return dataBodyEntity;
    }

    public static DataEnvelope createTestDataEnvelopeApiObject() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
        return dataEnvelope;
    }

    public static DataEnvelope createTestDataEnvelopeApiObjectWithEmptyName() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME_EMPTY, BlockTypeEnum.BLOCKTYPEA);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
        return dataEnvelope;
    }


    public static List<DataEnvelope> createTestListOfDataEnvelopeApiObject() {
        List<DataEnvelope> dataEnvelopeList = new ArrayList<>();
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

        dataEnvelopeList.add(dataEnvelope);
        dataEnvelopeList.add(dataEnvelope);
        dataEnvelopeList.add(dataEnvelope);
        return dataEnvelopeList;
    }

    public static List<DataBodyEntity> createTestListDataBodyEntity() {

        List<DataBodyEntity> dataBodyEntityList = new ArrayList<>();
        DataHeaderEntity dataHeaderEntity = null;
        DataBodyEntity dataBodyEntity = null;
        for (int i = 0; i < 3; i++) {
            dataHeaderEntity = new DataHeaderEntity();
            dataHeaderEntity.setName(TEST_NAME + i);
            dataHeaderEntity.setBlocktype(BlockTypeEnum.BLOCKTYPEA);

            dataBodyEntity = new DataBodyEntity();
            dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
            dataBodyEntity.setDataBody(DUMMY_DATA + i);
            dataBodyEntityList.add(dataBodyEntity);
        }
        return dataBodyEntityList;
    }
}
