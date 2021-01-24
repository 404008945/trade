package com.xishan.store.trade.server.es;

import com.github.pagehelper.PageInfo;
import com.xishan.store.escommon.EsUtil;
import com.xishan.store.escommon.model.Bulk;
import com.xishan.store.trade.api.request.FindOrderRequest;
import com.xishan.store.trade.api.response.OrderComplexDTO;
import com.xishan.store.trade.server.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建索引并dump全量数据。
 */
@Component
@Slf4j
public class EsInit {
    @Autowired
    private RestHighLevelClient client;

    private EsUtil esUtil;

    @Value("${orderIndex:order_index}")
    private String orderIndex;

    @Autowired
    private OrderService orderService;

    @PostConstruct
    public void init(){
        this.esUtil = new EsUtil();
        this.esUtil.setClient(client);
        if(!esUtil.indexExists(orderIndex)){
            //创建索引
            createOrderIndex();
            //全量dump数据
            dumpOrderIndex();

        }
    }


    public void createOrderIndex() {
        try {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(orderIndex);
            Map<String, Object> id = new HashMap<>();
            id.put("type", "integer");
            Map<String, Object> code = new HashMap<>();
            code.put("type", "text");
            Map<String, Object> type = new HashMap<>();
            type.put("type", "byte");
            Map<String, Object> customerId = new HashMap<>();
            customerId.put("type", "long");
            Map<String, Object> amount = new HashMap<>();
            amount.put("type", "long");
            Map<String, Object> paymentType = new HashMap<>();
            paymentType.put("type", "byte");
            Map<String, Object> status = new HashMap<>();
            status.put("type", "byte");
            Map<String, Object> userId = new HashMap<>();
            userId.put("type", "long");
            Map<String, Object> skuId = new HashMap<>();
            skuId.put("type", "integer");
            Map<String, Object> goodsId = new HashMap<>();
            goodsId.put("type", "integer");
            Map<String, Object> userName = new HashMap<>();
            userName.put("type", "text");
            Map<String, Object> goodName = new HashMap<>();
            goodName.put("type", "text");
            Map<String, Object> skuName = new HashMap<>();
            skuName.put("type", "text");
            Map<String, Object> createTime = new HashMap<>();
            createTime.put("type", "date");


            Map<String, Object> properties = new HashMap<>();
            properties.put("id", id);
            properties.put("code", code);
            properties.put("type", type);
            properties.put("customerId", customerId);
            properties.put("amount", amount);
            properties.put("paymentType", paymentType);
            properties.put("status", status);

            properties.put("userId", userId);

            properties.put("skuId", skuId);
            properties.put("goodsId", goodsId);
            properties.put("userName", userName);
            properties.put("goodName", goodName);
            properties.put("skuName", skuName);
            properties.put("createTime", createTime);
            Map<String, Object> mapping = new HashMap<>();
            mapping.put("properties", properties);
            createIndexRequest.mapping(mapping);
            client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 全量dump索引,通过分页，一次分页100,直到全部分页完成
     */
    public void dumpOrderIndex() {
        int pageNo = 0;
        PageInfo<Integer> pageInfo = orderService.pagingIds(pageNo, 100);
        List<Integer> ids = pageInfo.getList();
        queryAndIndex(ids);
        while (pageInfo.isHasNextPage()) {
            ids = pageInfo.getList();
            queryAndIndex(ids);
            pageNo++;
            pageInfo = orderService.pagingIds(pageNo, 100);
        }

    }

    public void queryAndIndex(List<Integer> ids){
        List<OrderComplexDTO> orders = new ArrayList<>();
        List<Bulk> bulks = new ArrayList<>();
        ids.stream().forEach(it->{
            FindOrderRequest findOrderRequest = new FindOrderRequest();
            findOrderRequest.setId(it);
            OrderComplexDTO goodComplexDTO =  orderService.findById(findOrderRequest);
            if(goodComplexDTO != null){
                log.info("dump good:{}",goodComplexDTO);
                orders.add(goodComplexDTO);
                Bulk bulk = new Bulk(String.valueOf(goodComplexDTO.getId()),goodComplexDTO);

                bulks.add(bulk);
            }
        });

        if (CollectionUtils.isEmpty(bulks)) {
            return;
        }
        esUtil.bulkIndex(orderIndex,bulks);
    }
}
