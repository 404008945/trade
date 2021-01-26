package com.xishan.store.trade.server.mapper;


import com.xishan.store.trade.api.model.AmountRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AmountRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AmountRecord record);

    int insertSelective(AmountRecord record);

    AmountRecord selectByPrimaryKey(Integer id);

    AmountRecord selectByCode(String code);

    List<AmountRecord> selectByUserId(Long id);

    int updateByPrimaryKeySelective(AmountRecord record);

    int updateByPrimaryKey(AmountRecord record);
}