package ken.tar.sa_backend.config;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;


@Component
public class LoggerFactory {
    public Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}
