package org.donglight.watchdog.server.config;

import org.donglight.watchdog.server.persistence.DatabaseWatchdogImpl;
import org.donglight.watchdog.server.persistence.JdbcUtil;
import org.donglight.watchdog.server.persistence.JsonWatchdogDaoImpl;
import org.donglight.watchdog.server.persistence.WatchdogDao;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * jdbc相关类的 配置类
 *
 * @author donglight
 * @date 2019/5/13
 * @since 1.0.0
 */

@Configuration
@ConditionalOnProperty(name = "watchdog.server.persistence-enabled", havingValue = "true")
public class WatchdogPersistenceConfiguration {

    @Bean
    @ConditionalOnProperty(name = "watchdog.server.persistence-type", havingValue = "json")
    public WatchdogDao watchdogDao() {
        return new JsonWatchdogDaoImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "watchdog.server.persistence-type", havingValue = "database", matchIfMissing = true)
    public JdbcUtil jdbcUtil() {
        return new JdbcUtil();
    }

    @Bean
    @ConditionalOnBean(JdbcUtil.class)
    public WatchdogDao databaseWatchdog() {
        return new DatabaseWatchdogImpl(jdbcUtil());
    }

}
