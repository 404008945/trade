package com.xishan.store.trade.server.mapper;


import com.xishan.store.trade.api.model.AmountRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AmountRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AmountRecord record);

    int insertSelective(AmountRecord record);

    AmountRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AmountRecord record);

    int updateByPrimaryKey(AmountRecord record);
}