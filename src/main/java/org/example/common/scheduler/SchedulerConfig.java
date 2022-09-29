package org.example.common.scheduler;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
//@EnableSchedulerLock(defaultLockAtMostFor = "10s")  //스케쥴 락을 사용하기 위한 설정, 최소 10초 이상은 Lock 시간을 보장한다.
public class SchedulerConfig {
//    @Bean
//    public LockProvider lockProvider(DataSource dataSource) {
//                return new JdbcTemplateLockProvider(
//                    JdbcTemplateLockProvider.Configuration.builder()
//                    .withJdbcTemplate(new JdbcTemplate(dataSource))
//                    .usingDbTime() // Works on Postgres, MySQL, MariaDb, MS SQL, Oracle, DB2, HSQL and H2
//                    .build()
//                );
//    }
}
