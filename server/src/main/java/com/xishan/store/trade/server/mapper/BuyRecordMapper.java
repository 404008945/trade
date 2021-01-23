package com.xishan.store.trade.server.mapper;


import com.xishan.store.trade.api.model.BuyRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BuyRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BuyRecord record);

    int insertSelective(BuyRecord record);

    BuyRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BuyRecord record);

    int updateByPrimaryKey(BuyRecord record);
}