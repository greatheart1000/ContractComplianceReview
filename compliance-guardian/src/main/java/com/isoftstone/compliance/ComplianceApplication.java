package com.isoftstone.compliance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession(maxInactiveIntervalInSeconds=60*60*24)
@SpringBootApplication(scanBasePackages = {"com.ljh.yqvalley", "com.isoftstone.compliance"})
@MapperScan({"com.ljh.yqvalley.**.dao", "com.isoftstone.compliance.**.dao"})
@ComponentScan({"com.ljh.yqvalley", "com.isoftstone.compliance"})
public class ComplianceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComplianceApplication.class, args);
	}

}
