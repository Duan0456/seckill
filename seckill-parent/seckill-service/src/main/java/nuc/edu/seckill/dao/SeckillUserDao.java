package nuc.edu.seckill.dao;

import java.util.List;
import nuc.edu.seckill.model.SeckillUser;
import nuc.edu.seckill.common.util.bean.CommonQueryBean;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 
 * SeckillUser数据库操作接口类
 * 
 **/

@Repository
@Mapper
public interface SeckillUserDao{


	/**
	 * 
	 * 查询（根据主键ID查询）
	 * 
	 **/
	SeckillUser  selectByPrimaryKey ( @Param("id") Long id );

	/**
	 * 
	 * 删除（根据主键ID删除）
	 * 
	 **/
	int deleteByPrimaryKey ( @Param("id") Long id );

	/**
	 * 
	 * 添加
	 * 
	 **/
	int insert( SeckillUser record );

	/**
	 * 
	 * 修改 （匹配有值的字段）
	 * 
	 **/
	int updateByPrimaryKeySelective( SeckillUser record );

	/**
	 * 
	 * list分页查询
	 * 
	 **/
	List<SeckillUser> list4Page ( SeckillUser record, @Param("commonQueryParam") CommonQueryBean query);

	/**
	 * 
	 * count查询
	 * 
	 **/
	int count ( SeckillUser record);

	/**
	 * 
	 * list查询
	 * 
	 **/
	List<SeckillUser> list ( SeckillUser record);

}