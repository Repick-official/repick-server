package repick.repickserver.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import repick.repickserver.domain.product.application.ProductService;


@Slf4j @Component
public class RedisKeyExpiredListener implements MessageListener {

    private final ProductService productService;

    @Autowired
    public RedisKeyExpiredListener(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        productService.HandleProductSmsPendingExpiration(message.toString());
    }
}

//@Component
//public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {
//
//    private final ProductService productService;
//
//    @Autowired
//    public RedisKeyExpiredListener(RedisMessageListenerContainer listenerContainer, ProductService productService) {
//        super(listenerContainer);
//        this.productService = productService;
//    }
//
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        productService.HandleProductSmsPendingExpiration(message.toString());
//    }
//}