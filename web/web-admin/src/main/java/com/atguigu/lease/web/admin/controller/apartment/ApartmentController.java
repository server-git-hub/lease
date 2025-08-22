package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.Exception.notDeleteException;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "公寓信息管理")
@RestController
@RequestMapping("/admin/apartment")
public class ApartmentController {

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Autowired
    private ApartmentFacilityService apartmentFacilityService;

    @Autowired
    private ApartmentLabelService apartmentLabelService;

    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private RoomInfoService roomInfoService;

    @Operation(summary = "保存或更新公寓信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ApartmentSubmitVo apartmentSubmitVo) {
        boolean flag=apartmentInfoService.customSaveOrUpdate(apartmentSubmitVo);
        return flag ? Result.ok() : Result.fail();
    }

    @Operation(summary = "根据条件分页查询公寓列表")
    @GetMapping("pageItem")
    public Result<IPage<ApartmentItemVo>> pageItem(@RequestParam long current, @RequestParam long size, ApartmentQueryVo queryVo) {
        IPage<ApartmentItemVo> page=new Page<>(current,size);
        apartmentInfoService.customList(page,queryVo);
        return Result.ok(page);
    }

    @Operation(summary = "根据ID获取公寓详细信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo apartmentDetailVo=apartmentInfoService.customById(id);
        return Result.ok(apartmentDetailVo);
    }

    @Operation(summary = "根据id删除公寓信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id){
        LambdaQueryWrapper<RoomInfo> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomInfo::getApartmentId,id);
        long count = roomInfoService.count(queryWrapper);
        if(count>0){
            throw new notDeleteException(ResultCodeEnum.DELETE_ERROR);
        }


        //删除公寓信息
        apartmentInfoService.removeById(id);
        //删除配套中间表信息
        LambdaQueryWrapper<ApartmentFacility> ApartmentFacilitylambdaQueryWrapper=new LambdaQueryWrapper<>();
        ApartmentFacilitylambdaQueryWrapper.eq(ApartmentFacility::getApartmentId,id);
        apartmentFacilityService.remove(ApartmentFacilitylambdaQueryWrapper);
        //删除图片信息
        LambdaQueryWrapper<GraphInfo> GraphInfolambdaQueryWrapper=new LambdaQueryWrapper<>();
        GraphInfolambdaQueryWrapper.eq(GraphInfo::getItemId,id);
        GraphInfolambdaQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphInfoService.remove(GraphInfolambdaQueryWrapper);
        //删除标签中间表信息
        LambdaQueryWrapper<ApartmentLabel> ApartmentLabellambdaQueryWrapper=new LambdaQueryWrapper<>();
        ApartmentLabellambdaQueryWrapper.eq(ApartmentLabel::getApartmentId,id);
        apartmentLabelService.remove(ApartmentLabellambdaQueryWrapper);
        //删除杂费中间表信息
        LambdaQueryWrapper<ApartmentFeeValue> ApartmentFeeValuelambdaQueryWrapper=new LambdaQueryWrapper<>();
        ApartmentFeeValuelambdaQueryWrapper.eq(ApartmentFeeValue::getApartmentId,id);
        apartmentFeeValueService.remove(ApartmentFeeValuelambdaQueryWrapper);
        return Result.ok();
    }

    @Operation(summary = "根据id修改公寓发布状态")
    @PostMapping("updateReleaseStatusById")
    public Result updateReleaseStatusById(@RequestParam Long id, @RequestParam ReleaseStatus status) {
        LambdaUpdateWrapper<ApartmentInfo> lambdaUpdateWrapper=new LambdaUpdateWrapper();
        lambdaUpdateWrapper.eq(ApartmentInfo::getId,id);
        lambdaUpdateWrapper.set(ApartmentInfo::getIsRelease,status);
        apartmentInfoService.update(lambdaUpdateWrapper);
        return Result.ok();
    }

    @Operation(summary = "根据区县id查询公寓信息列表")
    @GetMapping("listInfoByDistrictId")
    public Result<List<ApartmentInfo>> listInfoByDistrictId(@RequestParam Long id) {
        LambdaQueryWrapper<ApartmentInfo> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(ApartmentInfo::getDistrictId,id);
        List<ApartmentInfo> list = apartmentInfoService.list(lambdaQueryWrapper);
        return Result.ok(list);
    }
}














