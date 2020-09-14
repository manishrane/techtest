package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public interface DataStoreRepository extends JpaRepository<DataBodyEntity, Long> {



//    public List<DataBodyEntity> findDataBodyEntitiesByDataHeaderEntityIsContaining() ;


    @Query("SELECT d FROM DataBodyEntity d JOIN d.dataHeaderEntity h WHERE h.blocktype = ?1")
    public List<DataBodyEntity> findDataByBlockType(BlockTypeEnum    blockType);


    @Query("SELECT d FROM DataBodyEntity d JOIN d.dataHeaderEntity h WHERE h.name = ?1")
    public Optional<DataBodyEntity> findDataByBlockName(String    blockName);



}
