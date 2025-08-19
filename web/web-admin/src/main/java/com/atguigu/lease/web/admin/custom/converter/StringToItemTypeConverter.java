package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.ItemType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToItemTypeConverter implements Converter<String, ItemType> {


    //做类型转换的方法
    @Override
    public ItemType convert(String source) {

        ItemType[] values = ItemType.values();
        for(ItemType itemType : values) {
            if(source.equals(itemType.getCode()+"")){
                return itemType;
            }
        }
        throw new RuntimeException("数据类型输入错误，只能输入1或2");
    }
}
