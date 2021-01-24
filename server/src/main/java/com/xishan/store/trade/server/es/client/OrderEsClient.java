package com.xishan.store.trade.server.es.client;

import com.google.common.collect.Lists;
import com.xishan.store.base.exception.ServiceException;
import com.xishan.store.escommon.EsUtil;
import com.xishan.store.escommon.model.Bulk;
import com.xishan.store.escommon.page.EsPage;
import com.xishan.store.trade.api.response.OrderComplexDTO;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现goods在es中的增删改
 */
@Component
public class OrderEsClient  implements InitializingBean {
    @Autowired
    private RestHighLevelClient client;

    @Value("${orderIndex:order_index}")
    private String orderIndex;

    private EsUtil esUtil;
    @Override
    public void afterPropertiesSet() throws Exception {
        this.esUtil = new EsUtil();
        this.esUtil.setClient(client);
    }

    public void index(OrderComplexDTO orderComplexDTO){
        if(orderComplexDTO == null){
            throw  new ServiceException("goodComplexDTO不可为空");
        }
        Bulk bulk = new Bulk(orderComplexDTO.getId().toString(),orderComplexDTO);
        esUtil.index(orderIndex,bulk);
    }

    //删除
    public void deleteById(OrderComplexDTO orderComplexDTO){
        if(orderComplexDTO == null){
            throw  new ServiceException("goodComplexDTO不可为空");
        }
        esUtil.delete(orderIndex,orderComplexDTO.getId().toString());
    }

    //根据id查找

    public OrderComplexDTO getById(OrderComplexDTO goodComplexDTO){
        if(goodComplexDTO == null){
            throw  new ServiceException("goodComplexDTO不可为空");
        }
        return esUtil.getById(orderIndex,goodComplexDTO.getId().toString(),OrderComplexDTO.class);
    }


    /**
     * 多条件检索并集，适用于搜索比如包含腾讯大王卡，滴滴大王卡的用户
     * @param fieldKey
     * @param queryList
     * @return
     */
    public BoolQueryBuilder orMatchUnionWithList(String fieldKey, List<String> queryList){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (String fieldValue : queryList) {
            boolQuery.should(QueryBuilders.matchPhraseQuery(fieldKey, fieldValue));
        }
        return boolQuery;
    }

    /**
     * term单条件
     * @param fieldKey
     * @param value
     * @return
     */
    public BoolQueryBuilder onTermWithSingle(String fieldKey,  Integer value){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.termQuery(fieldKey, value));
        return boolQuery;
    }

    public List<OrderComplexDTO> listAll(OrderComplexDTO orderComplexDTO){
        if(orderComplexDTO == null){
            throw  new ServiceException("goodComplexDTO不可为空");
        }
        BoolQueryBuilder mustQuery = QueryBuilders.boolQuery();
        makeCretiria(mustQuery,orderComplexDTO);
        return esUtil.listAll(orderIndex,mustQuery,OrderComplexDTO.class);
    }
    public EsPage<OrderComplexDTO> paging(OrderComplexDTO orderComplexDTO,int pageNo ,int pageSize){
        if(orderComplexDTO == null){
            throw  new ServiceException("goodComplexDTO不可为空");
        }
        BoolQueryBuilder mustQuery = QueryBuilders.boolQuery();
        makeCretiria(mustQuery,orderComplexDTO);
        return esUtil.paging(orderIndex,mustQuery,pageNo,pageSize,OrderComplexDTO.class);
    }

    private void makeCretiria(BoolQueryBuilder boolQueryBuilder,OrderComplexDTO orderComplexDTO){

        if(orderComplexDTO.getSkuName()!=null) {
            boolQueryBuilder.must(orMatchUnionWithList("skuName", Lists.newArrayList(orderComplexDTO.getSkuName())));
        }
        if(orderComplexDTO.getGoodName()!=null) {
            boolQueryBuilder.must(orMatchUnionWithList("goodName", Lists.newArrayList(orderComplexDTO.getGoodName())));
        }
        if(orderComplexDTO.getUserName() != null) {
            boolQueryBuilder.must(orMatchUnionWithList("cateName", Lists.newArrayList(orderComplexDTO.getUserName())));
        }
        if(orderComplexDTO.getId() != null) {
            boolQueryBuilder.must(onTermWithSingle("id",orderComplexDTO.getId()));
        }
        if(orderComplexDTO.getGoodsId() != null) {
            boolQueryBuilder.must(onTermWithSingle("goodsId",orderComplexDTO.getGoodsId()));
        }
        if(orderComplexDTO.getCode() != null) {
            boolQueryBuilder.must(orMatchUnionWithList("code", Lists.newArrayList(orderComplexDTO.getCode())));
        }
    }

    public void batchUpdate(List<OrderComplexDTO> orderComplexDTOS){
        List<Bulk> bulks = new ArrayList<>();
        orderComplexDTOS.forEach(it->{
            bulks.add(new Bulk(String.valueOf(it.getId()),it));
        });
        esUtil.bulkIndex(orderIndex,bulks);
    }
}