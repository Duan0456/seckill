package nuc.edu.seckill.controller;


import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.common.util.bean.CommonQueryBean;
import nuc.edu.seckill.model.SeckillProducts;
import nuc.edu.seckill.service.ISeckillProductsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/product")
@Slf4j
public class SeckillProductsController {
    @Autowired
    private ISeckillProductsService seckillProductsService;

    @RequestMapping("/listPageSeckillProducts")
    public String listPageSeckillProducts(Model model, String name,
                                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        SeckillProducts searchItem = new SeckillProducts();
        if (StringUtils.isEmpty(name)) {
            searchItem.setName("name");
            model.addAttribute("name", name);
        }

        CommonQueryBean queryBean = new CommonQueryBean();
        queryBean.setPageSize(pageSize);
        queryBean.setStart((pageNum - 1) * pageSize);
        List<SeckillProducts> seckillProductsList = seckillProductsService.list4Page(searchItem, queryBean);
        long total = seckillProductsService.count(searchItem);
        int totalPageNum = (int) total / pageSize;
        if (total % pageSize > 0) totalPageNum++;
        model.addAttribute("total", total);
        model.addAttribute("totalPage", totalPageNum);
        model.addAttribute("list", seckillProductsList);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pageNum", pageNum);

        return "product/listPageSeckillProducts";
    }




    /**
     * 进入创建product元素的界面.
     */
    @RequestMapping("/beforeCreateProduct")
    public String beforeCreateProduct() {
        return "product/beforeCreateProduct";
    }

    /**
     * 处理插入
     */
    @RequestMapping("/doCreateProduct")
    public String doCreateProduct(String uniqueId, String name,
                                  String startBuyTimeStr, Integer amount, String desc) throws Exception {
        Assert.notNull(uniqueId, "唯一id不能为空");
        Assert.notNull(name, "姓名不能为空");
        Assert.notNull(startBuyTimeStr, "开始购买时间不能为空");
        Assert.notNull(amount, "数量不能为空");

        SeckillProducts seckillProducts = new SeckillProducts();
        seckillProducts.setName(name);
        seckillProducts.setStartBuyTime(DateUtils.parseDate(startBuyTimeStr, "yyyy-MM-dd HH:mm:SS"));
        seckillProducts.setCount(amount);
        seckillProducts.setProductPeriodKey(uniqueId);
        if (StringUtils.isNotEmpty(desc)) {
            seckillProducts.setProductDesc(desc);
        }
        seckillProductsService.uniqueInsert(seckillProducts);//唯一插入（亮点）
        return "redirect:listPageSeckillProducts?isSave=yes";//提示保存成功
    }

    /**
     * 进入更新product的界面.
     */
    @RequestMapping("/beforeUpdateProduct")
    public String beforeUpdateProduct(Model model,Long id) {
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);
        if (seckillProducts != null) {
            model.addAttribute("item", seckillProducts);
        }
        return "product/beforeUpdateProduct";
    }

    /**
     * 实际创建或更新product的业务逻辑.
     */
    @RequestMapping("/doUpdateProduct")
    public String doUpdateProduct(Long id, String name,
                                  String startBuyTimeStr, Integer amount, String desc) throws Exception{
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);
        if (StringUtils.isNotEmpty(name)) seckillProducts.setName(name);
        if (StringUtils.isNotEmpty(startBuyTimeStr)) {
            seckillProducts.setStartBuyTime(DateUtils.parseDate(startBuyTimeStr, "yyyy-MM-dd HH:mm:SS"));
        }
        if (amount != null) seckillProducts.setCount(amount);
        if (StringUtils.isNotEmpty(desc)) seckillProducts.setProductDesc(desc);
        seckillProducts.setUpdatedTime(new Date());
        seckillProductsService.updateByPrimaryKeySelective(seckillProducts);
        return "redirect:listPageSeckillProducts";
    }

    /**
     * 查看product的元素信息，详情页面中所有元素都展示.
     */
    @RequestMapping("/showProductItem")
    public String showProductItem(Model model,Long id) {
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);
        if (seckillProducts != null) {
            model.addAttribute("item", seckillProducts);
        }
        return "product/showProductItem";
    }


    /**
     * 更新productStatus状态
     */
    @RequestMapping("/updateProductStatus")
    public String updateProductStatus(Long id, Integer status) {
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);
        seckillProducts.setStatus(status);
        //被删除的，不能修改回来
        if (SeckillProducts.STATUS_IS_ONLINE.equals(status) && SeckillProducts.IS_DEALED.equals(seckillProducts.getIsDeleted())) {
            seckillProducts.setIsDeleted(0);
        }
        seckillProducts.setUpdatedTime(new Date());
        seckillProductsService.updateByPrimaryKeySelective(seckillProducts);
        return "redirect:listPageSeckillProducts";

    }


    /**
     * 逻辑删除相关的秒杀product信息.
     */
    @RequestMapping("/deleteProduct")
    public String deleteProduct(Long id) {
        //不直接使用物理删除，改成更新isDeleted字段，改成逻辑update
        SeckillProducts seckillProducts = seckillProductsService.selectByPrimaryKey(id);
        seckillProducts.setIsDeleted(SeckillProducts.IS_DEALED);
        seckillProducts.setUpdatedTime(new Date());
        seckillProductsService.updateByPrimaryKeySelective(seckillProducts);
        return "redirect:listPageSeckillProducts";
    }

}
