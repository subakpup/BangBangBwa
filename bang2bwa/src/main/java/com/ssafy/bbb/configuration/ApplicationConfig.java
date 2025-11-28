package com.ssafy.bbb.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import com.ssafy.bbb.model.dao.ProductDao;

/**
 * DAO를 Mapper scan하기 위한 설정 파일.
 */
@Configuration
@MapperScan(basePackageClasses = { ProductDao.class })
public class ApplicationConfig {

}
