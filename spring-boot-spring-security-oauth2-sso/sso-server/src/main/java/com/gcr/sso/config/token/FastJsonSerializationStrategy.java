package com.gcr.sso.config.token;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;
import org.springframework.stereotype.Component;

/**
 * @author litz-a
 */
@Component
public class FastJsonSerializationStrategy extends StandardStringSerializationStrategy {

  @Setter
  @Getter
  private FastJsonRedisSerializer serializer = new FastJsonRedisSerializer<>(Object.class);

  @Override
  protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
    return (T) serializer.deserialize(bytes);
  }

  @Override
  protected byte[] serializeInternal(Object object) {
    return serializer.serialize(object);
  }
}
