package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Autowired
    private ApartmentFacilityService apartmentFacilityService;

    @Autowired
    private ApartmentLabelService apartmentLabelService;

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private ProvinceInfoService provinceInfoService;

    @Autowired
    private CityInfoService cityInfoService;

    @Autowired
    private DistrictInfoService districtInfoService;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Override
    public boolean customSaveOrUpdate(ApartmentSubmitVo apartmentSubmitVo) {
        Long apartmentid = apartmentSubmitVo.getId();
        boolean flag;
        if(apartmentid!=null) flag=true;
        else flag=false;

        //保存公寓信息 apartment_info
        apartmentSubmitVo.setProvinceName(provinceInfoService.getById(apartmentSubmitVo.getProvinceId()).getName());
        apartmentSubmitVo.setCityName(cityInfoService.getById(apartmentSubmitVo.getCityId()).getName());
        apartmentSubmitVo.setDistrictName(districtInfoService.getById(apartmentSubmitVo.getDistrictId()).getName());
        saveOrUpdate(apartmentSubmitVo);
        //保存杂费信息
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if(feeValueIds!=null && feeValueIds.size()>0){
            List<ApartmentFeeValue> list=new ArrayList<>(feeValueIds.size());
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue=ApartmentFeeValue.builder()
                        .apartmentId(apartmentid)
                        .feeValueId(feeValueId)
                        .build();
                list.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(list);
        }
        //保存配套信息
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if(facilityInfoIds!=null && facilityInfoIds.size()>0){
            List<ApartmentFacility> list=new ArrayList<>(facilityInfoIds.size());
            for (Long facilityInfoId : facilityInfoIds) {
                ApartmentFacility build = ApartmentFacility.builder().
                        apartmentId(apartmentid).facilityId(facilityInfoId).build();
                list.add(build);
            }
            apartmentFacilityService.saveBatch(list);
        }
        //保存标签信息
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if(labelIds!=null && labelIds.size()>0){
            List<ApartmentLabel> list=new ArrayList<>(labelIds.size());
            for (Long labelId : labelIds) {
                ApartmentLabel build = ApartmentLabel.builder()
                        .apartmentId(apartmentid).labelId(labelId).build();
                list.add(build);
            }
            apartmentLabelService.saveBatch(list);
        }


        //保存图片信息
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if(graphVoList!=null && graphVoList.size()>0){
            List<GraphInfo> list=new ArrayList<>(graphVoList.size());
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo=new GraphInfo();
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfo.setItemId(apartmentid);
                graphInfo.setItemType(ItemType.APARTMENT);
                list.add(graphInfo);
            }
            graphInfoService.saveBatch(list);
        }


        if(flag){
            //删除杂费中间表
            LambdaQueryWrapper<ApartmentFeeValue> FeeValuequeryWrapper = new LambdaQueryWrapper<>();
            FeeValuequeryWrapper.eq(ApartmentFeeValue::getApartmentId,apartmentid);
            apartmentFeeValueService.remove(FeeValuequeryWrapper);
            //删除配套中间表
            LambdaQueryWrapper<ApartmentFacility> FacilityqueryWrapper = new LambdaQueryWrapper<>();
            FacilityqueryWrapper.eq(ApartmentFacility::getApartmentId,apartmentid);
            apartmentFacilityService.remove(FacilityqueryWrapper);
            //删除标签中间表
            LambdaQueryWrapper<ApartmentLabel> LabelqueryWrapper = new LambdaQueryWrapper<>();
            LabelqueryWrapper.eq(ApartmentLabel::getApartmentId,apartmentid);
            apartmentLabelService.remove(LabelqueryWrapper);
            //删除图片中间表
            LambdaQueryWrapper<GraphInfo> GraphQueryWrapper = new LambdaQueryWrapper<>();
            GraphQueryWrapper.eq(GraphInfo::getItemId,apartmentid);
            graphInfoService.remove(GraphQueryWrapper);
        }
        return true;
    }

    @Override
    public IPage<ApartmentItemVo> customList(IPage<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.customAll(page,queryVo);

    }
}




