package nuc.edu.seckill.dao;

import nuc.edu.seckill.common.util.bean.CommonQueryBean;
import nuc.edu.seckill.model.Demo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Demo数据库操作接口类
 **/

@Repository
@Mapper
public interface DemoDao {


    /**
     * 查询（根据主键ID查询）
     **/
    Demo selectByPrimaryKey(@Param("id") Long id);

    /**
     * 删除（根据主键ID删除）
     **/
    int deleteByPrimaryKey(@Param("id") Long id);

    /**
     * 添加
     **/
    int insert(Demo record);

    /**
     * 修改 （匹配有值的字段）
     **/
    int updateByPrimaryKeySelective(Demo record);

    /**
     * list分页查询
     **/
    List<Demo> list4Page(@Param("record") Demo record, @Param("commonQueryParam") CommonQueryBean query);

    /**
     * count查询
     **/
    int count(Demo record);

    /**
     * list查询
     **/
    List<Demo> list(Demo record);

}