package nuc.edu.seckill.service.impl;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.common.util.bean.CommonQueryBean;
import nuc.edu.seckill.dao.SeckillProductsDao;
import nuc.edu.seckill.model.SeckillProducts;
import nuc.edu.seckill.service.ISeckillProductsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service("seckillProductsService")
@Slf4j
public class SeckillProductsServiceImpl implements ISeckillProductsService {

    @Autowired
    private SeckillProductsDao seckillProductsDao;

    @Override
    public SeckillProducts selectByPrimaryKey(Long id) {
        return seckillProductsDao.selectByPrimaryKey(id);
    }

    @Override
    public int insert(SeckillProducts record) {
        return seckillProductsDao.insert(record);
    }

    @Override
    public int updateByPrimaryKeySelective(SeckillProducts record) {
        return seckillProductsDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<SeckillProducts> list4Page(SeckillProducts record, CommonQueryBean query) {
        return seckillProductsDao.list4Page(record, query);
    }

    @Override
    public long count(SeckillProducts record) {
        return seckillProductsDao.count(record);
    }

    @Override
    public List<SeckillProducts> list(SeckillProducts record) {
        return seckillProductsDao.list(record);
    }

    @Override
    public Long uniqueInsert(SeckillProducts record) {

        try {
            record.setCreateTime(new Date());
            record.setIsDeleted(0);
            record.setStatus(SeckillProducts.STATUS_IS_OFFLINE);

            SeckillProducts existItem = findByProductPeriodKey(record.getProductPeriodKey());
            if (existItem != null) return existItem.getId();
            else seckillProductsDao.insert(record);
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("Duplicate entry") >= 0) {
                SeckillProducts existItem = findByProductPeriodKey(record.getProductPeriodKey());
                return existItem.getId();
            } else {
                log.error(ex.getMessage(), ex);
                throw new RuntimeException(ex.getMessage());
            }
        }


        return null;
    }

    @Override
    public SeckillProducts findByProductPeriodKey(String productPeriodKey) {
        Assert.isTrue(!StringUtils.isEmpty(productPeriodKey));

        SeckillProducts item = new SeckillProducts();
        item.setProductPeriodKey(productPeriodKey);

        List<SeckillProducts> list = seckillProductsDao.list(item);
        if (CollectionUtils.isEmpty(list)) return null;
        else{
            return list.get(0);
        }
    }
}
