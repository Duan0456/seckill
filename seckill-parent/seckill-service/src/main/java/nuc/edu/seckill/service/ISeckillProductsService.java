package nuc.edu.seckill.service;

import nuc.edu.seckill.common.util.bean.CommonQueryBean;
import nuc.edu.seckill.model.SeckillProducts;

import java.util.List;

/**
 *
 * SeckillProductsservice层接口类
 *
 **/

public interface ISeckillProductsService {

    /**
     *
     * 查询（根据主键ID查询）
     *
     **/
    SeckillProducts  selectByPrimaryKey(Long id);

    /**
     *
     * 添加
     *
     **/
    int insert(SeckillProducts record);
    /**
     *
     * 修改 （匹配有值的字段）
     *
     **/
    int updateByPrimaryKeySelective(SeckillProducts record);

    /**
     *
     * list分页查询
     *
     **/
    List<SeckillProducts> list4Page(SeckillProducts record, CommonQueryBean query);

    /**
     *
     * count查询
     *
     **/
    long count(SeckillProducts record);

    /**
     *
     * list查询
     *
     **/
    List<SeckillProducts> list(SeckillProducts record);

    /**
     * 唯一索引保证新增的数据唯一.
     */
    Long uniqueInsert(SeckillProducts record);

    /**
     *
     */
    SeckillProducts findByProductPeriodKey(String productPeriodKey);
}
