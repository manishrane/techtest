package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.BlockNotFoundException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.util.ChecksumGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final ModelMapper modelMapper;

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope, String checksum) {

        // Save to persistence.
        if (checksum.equals(ChecksumGenerator.getCheckSum(envelope.getDataBody().getDataBody()))) {

            persist(envelope);

            log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
            return true;
        }
        return false;
    }

    @Override
    public List<DataEnvelope> getData(BlockTypeEnum blockType) {

        List<DataBodyEntity> list = dataBodyServiceImpl.getDataByBlockType(blockType);

        List<DataEnvelope> dataEnvelopes = list.stream().map(dataBodyEntity -> {
            DataBody dataBody = modelMapper.map(dataBodyEntity, DataBody.class);
            DataHeader dataHeader = modelMapper.map(dataBodyEntity.getDataHeaderEntity(), DataHeader.class);
            DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
            return dataEnvelope;
        }).collect(Collectors.toList());


        return dataEnvelopes;

    }

    @Override
    public boolean updateDataByBlockName(String blockName , BlockTypeEnum blockTypeEnum) {

        dataBodyServiceImpl.getDataByBlockName(blockName)
                .map(dataBodyEntity -> {
                    dataBodyEntity.getDataHeaderEntity().setBlocktype(blockTypeEnum);
                    saveData(dataBodyEntity);
                    return true;
                })
                .orElseThrow(() -> new BlockNotFoundException("No block found with the Name :" + blockName))
        ;


        return true;
    }


    private void persist(DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

        saveData(dataBodyEntity);
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

}


