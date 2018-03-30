package com.obsidiandynamics.yconf.sample.inline;

import org.slf4j.*;

import com.obsidiandynamics.yconf.*;

@Y
public final class LogConfig {
  @YInject(mapper=LoggerMapper.class)
  private Logger logger;
  
  public Logger getLogger() {
    return logger;
  }
  
  public static final class LoggerMapper implements TypeMapper {
    @Override
    public Object map(YObject y, Class<?> type) {
      return LoggerFactory.getLogger(y.map(String.class));
    }
  }
}
