package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface Server {
    boolean saveDataEnvelope(DataEnvelope envelope, String checksum) throws IOException, NoSuchAlgorithmException;

    List<DataEnvelope> getData(BlockTypeEnum blockTypeEnum);

    boolean updateDataByBlockName(String blockName, BlockTypeEnum blockTypeEnum);


}
