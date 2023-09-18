package repick.repickserver.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import repick.repickserver.domain.product.application.ProductService;

@Component
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

    private final ProductService productService;

    @Autowired
    public RedisKeyExpiredListener(RedisMessageListenerContainer listenerContainer, ProductService productService) {
        super(listenerContainer);
        this.productService = productService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        productService.HandleProductSmsPendingExpiration(message.toString());
    }
}