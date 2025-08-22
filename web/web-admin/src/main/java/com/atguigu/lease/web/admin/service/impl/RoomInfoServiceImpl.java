package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.RoomInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {
    @Autowired
    private RoomAttrValueService roomAttrValueService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Autowired
    private RoomLeaseTermService roomLeaseTermService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private RoomFacilityService roomFacilityService;

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Override
    public void roomSaveOrUpdate(RoomSubmitVo roomSubmitVo) {
        //房间信息
        saveOrUpdate(roomSubmitVo);
        Long roomId = roomSubmitVo.getId();
        //租期关系信息
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
        if(leaseTermIds!=null && leaseTermIds.size()>0){
            List<RoomLeaseTerm> list=new ArrayList<>(leaseTermIds.size());
            for (Long leaseTermId : leaseTermIds) {
                RoomLeaseTerm roomLeaseTerm =
                        RoomLeaseTerm.builder().roomId(roomId).leaseTermId(leaseTermId).build();
                list.add(roomLeaseTerm);
            }
            roomLeaseTermService.saveBatch(list);
        }
        //支付方式信息
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if(paymentTypeIds!=null && paymentTypeIds.size()>0){
            List<RoomPaymentType> list=new ArrayList<>(paymentTypeIds.size());
            for (Long paymentTypeId : paymentTypeIds) {
                RoomPaymentType roomPaymentType = RoomPaymentType.builder().roomId(roomId).paymentTypeId(paymentTypeId).build();
                list.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(list);
        }
        //属性值信息
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if(attrValueIds!=null && attrValueIds.size()>0){
            List<RoomAttrValue> list=new ArrayList<>(attrValueIds.size());
            for (Long attrValueId : attrValueIds) {
                RoomAttrValue roomAttrValue = RoomAttrValue.builder().roomId(roomId).attrValueId(attrValueId).build();
                list.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(list);
        }

        //标签信息
        List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
        if(labelInfoIds!=null && labelInfoIds.size()>0){
            List<RoomLabel> list=new ArrayList<>(labelInfoIds.size());
            for (Long labelInfoId : labelInfoIds) {
                list.add(RoomLabel.builder().roomId(roomId).labelId(labelInfoId).build());
            }
            roomLabelService.saveBatch(list);
        }
        //配套信息
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
        if(facilityInfoIds!=null && facilityInfoIds.size()>0){
            List<RoomFacility> list=new ArrayList<>(facilityInfoIds.size());
            for (Long facilityInfoId : facilityInfoIds) {
                list.add(RoomFacility.builder().roomId(roomId).facilityId(facilityInfoId).build());
            }
            roomFacilityService.saveBatch(list);
        }
        //图片信息
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if(graphVoList!=null && graphVoList.size()>0){
            List<GraphInfo> list=new ArrayList<>(graphVoList.size());
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo=new GraphInfo();
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setUrl(graphVo.getUrl());
                graphInfo.setName(graphVo.getName());
                graphInfo.setItemId(roomId);
                list.add(graphInfo);
            }
            graphInfoService.saveBatch(list);
        }


        if(roomId!=null){
            //删除房间信息
            removeById(roomId);
            //租期关系信息
            LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermqueryWrapper = new LambdaQueryWrapper<>();
            roomLeaseTermqueryWrapper.eq(RoomLeaseTerm::getRoomId,roomId);
            roomLeaseTermService.remove(roomLeaseTermqueryWrapper);
            //支付方式信息
            LambdaQueryWrapper<RoomPaymentType> roomPaymentTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomPaymentTypeLambdaQueryWrapper.eq(RoomPaymentType::getRoomId,roomId);
            roomPaymentTypeService.remove(roomPaymentTypeLambdaQueryWrapper);
            //标签信息
            LambdaQueryWrapper<RoomLabel> roomLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomLabelLambdaQueryWrapper.eq(RoomLabel::getRoomId,roomId);
            roomLabelService.remove(roomLabelLambdaQueryWrapper);

            //配套信息
            LambdaQueryWrapper<RoomFacility> roomFacilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomFacilityLambdaQueryWrapper.eq(RoomFacility::getRoomId,roomId);
            roomFacilityService.remove(roomFacilityLambdaQueryWrapper);
            //图片信息
            LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemId,roomId);
            graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemType,ItemType.ROOM);
            graphInfoService.remove(graphInfoLambdaQueryWrapper);
            //属性值信息
            LambdaQueryWrapper<RoomAttrValue> roomAttrValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomAttrValueLambdaQueryWrapper.eq(RoomAttrValue::getRoomId,roomId);
            roomAttrValueService.remove(roomAttrValueLambdaQueryWrapper);


        }
    }

    @Override
    public void customPage(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        page=roomInfoMapper.customPage(page,queryVo);
    }
}




