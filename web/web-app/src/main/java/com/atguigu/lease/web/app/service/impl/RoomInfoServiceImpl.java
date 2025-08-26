package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.*;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;
    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;
    @Autowired
    private GraphInfoService graphInfoService;
    @Autowired
    private GraphInfoMapper graphInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private AttrValueMapper attrValueMapper;
    @Autowired
    private FacilityInfoMapper facilityInfoMapper;
    @Autowired
    private FeeValueMapper feeValueMapper;
    @Autowired
    private PaymentTypeMapper paymentTypeMapper;
    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Override
    public RoomDetailVo customById(Long id) {
        RoomDetailVo roomDetailVo = new RoomDetailVo();
        //查询房间id对应的公寓信息
        ApartmentInfo apartmentInfo=apartmentInfoMapper.ByroomId(id);
        //查询公寓的图片信息
        LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
        graphInfoWrapper.eq(GraphInfo::getItemId,apartmentInfo.getId());
        graphInfoWrapper.eq(GraphInfo::getItemType,ItemType.APARTMENT);
        List<GraphInfo> graphInfoList = graphInfoService.list(graphInfoWrapper);
        //查询公寓的标签信息
        List<LabelInfo> labelInfoList=labelInfoMapper.customLabel(apartmentInfo.getId());
        //查询公寓的所有房间中租金最低的租金值信息
        BigDecimal bigDecimal=apartmentInfoMapper.selectMinRent(apartmentInfo.getId());
        //装载公寓信息
        ApartmentItemVo apartmentItemVo=new ApartmentItemVo();
        apartmentItemVo.setGraphVoList(graphInfoList);
        apartmentItemVo.setLabelInfoList(labelInfoList);
        apartmentItemVo.setMinRent(bigDecimal);
        BeanUtils.copyProperties(apartmentInfo,apartmentItemVo);
        //查询房间信息
        RoomInfo roomInfo = getById(id);
        //查询房间图片信息
        List<GraphVo> graphVoList=graphInfoMapper.ByroomId(id);
        //查询房间属性信息
        List<AttrValueVo> attrValueVoRoomList=attrValueMapper.selectAll(id);
        //查询房间标签信息
        List<LabelInfo> labelInfoRoomList=labelInfoMapper.customByRoomId(id);
        //查询房间杂费信息
        List<FeeValueVo> feeValueVo=feeValueMapper.selectByroomId(id);
        //查询房间配套信息
        List<FacilityInfo> facilityInfo=facilityInfoMapper.ByroomId(id);
        //查询房间支付方式信息
        List<PaymentType> paymentTypeRoomList=paymentTypeMapper.ByroomId(id);
        //查询房间租期信息
        List<LeaseTerm> leaseTermRoomList=leaseTermMapper.ByroomId(id);
        //查询房间入住信息
        Boolean status=roomInfoMapper.ByStatus(id);
        //查询房间是否被删除
        Boolean is_deleted= false;
        LambdaQueryWrapper<RoomInfo> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoomInfo::getId,id);
        long count = count(lambdaQueryWrapper);
        if(count==0) is_deleted=true;

        //装配房间信息
        roomDetailVo.setLabelInfoList(labelInfoRoomList);
        roomDetailVo.setApartmentItemVo(apartmentItemVo);
        roomDetailVo.setFacilityInfoList(facilityInfo);
        roomDetailVo.setFeeValueVoList(feeValueVo);
        roomDetailVo.setGraphVoList(graphVoList);
        roomDetailVo.setAttrValueVoList(attrValueVoRoomList);
        roomDetailVo.setLeaseTermList(leaseTermRoomList);
        roomDetailVo.setPaymentTypeList(paymentTypeRoomList);
        roomDetailVo.setIsCheckIn(status);
        roomDetailVo.setIsDelete(is_deleted);
        BeanUtils.copyProperties(roomInfo,roomDetailVo);
        return roomDetailVo;
    }

    @Override
    public void customPage(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        roomInfoMapper.getPage(page,queryVo);
    }

    @Override
    public void customPageByApartmentId(IPage<RoomItemVo> page, Long id) {
        roomInfoMapper.getByApartmentIdPage(page,id);
    }
}




