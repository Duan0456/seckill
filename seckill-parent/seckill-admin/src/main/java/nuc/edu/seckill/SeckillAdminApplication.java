package nuc.edu.seckill;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "nuc.edu")
@EnableScheduling
@MapperScan(value = "nuc.edu.seckill.dao")
public class SeckillAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillAdminApplication.class,args);
    }
}
