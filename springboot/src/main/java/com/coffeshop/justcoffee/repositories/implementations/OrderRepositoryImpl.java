package com.coffeshop.justcoffee.repositories.implementations;

import com.coffeshop.justcoffee.models.CoffeeOrder;
import com.coffeshop.justcoffee.models.Order;
import com.coffeshop.justcoffee.repositories.interfaces.CoffeeOrderRepository;
import com.coffeshop.justcoffee.repositories.interfaces.OrderRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.coffeshop.justcoffee.utils.IdGenerator.generateId;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private static final String KEY = "ORDER";
    private final RedisTemplate<String, Order> orderTemplate;
    private final CoffeeOrderRepository coffeeOrderRepository;
    private HashOperations orderHashOperations;

    public OrderRepositoryImpl(RedisTemplate<String, Order> orderTemplate, CoffeeOrderRepository coffeeOrderRepository) {
        this.orderTemplate = orderTemplate;
        this.coffeeOrderRepository = coffeeOrderRepository;
    }

    @PostConstruct
    private void init() {
        orderHashOperations = orderTemplate.opsForHash();
    }

    @Override
    public Collection<Order> findAllOrders() {
        return (Collection<Order>) orderHashOperations.entries(KEY).values();
    }

    @Override
    public long createOrder() {
        Order order = new Order();
        long generatedId = generateId();
        order.setId(generatedId);
        order.setNow(LocalDateTime.now());
        orderHashOperations.put(KEY, generatedId, order);
        return generatedId;
    }

    @Override
    public Order getOrderById(long orderId) {
        return (Order) orderHashOperations.get(KEY, orderId);
    }

    @Override
    public List<CoffeeOrder> getCoffeeDrinksByOrderId(long orderId) {
        Order order = (Order) orderHashOperations.get(KEY, orderId);
        List<Long> coffeeOrdersId = order.getCoffeeDrinks();
        return coffeeOrdersId.stream()
                .map(id -> coffeeOrderRepository.getCoffeeOrderById(id))
                .collect(Collectors.toList());
    }

    @Override
    public void addCoffeeOrderToOrder(long orderId, long coffeeOrderId) {
        Order order = (Order) orderHashOperations.get(KEY, orderId);
        order.addCoffeeOrder(coffeeOrderId);
        orderHashOperations.delete(KEY, orderId);
        orderHashOperations.put(KEY, orderId, order);
    }

    @Override
    public void deleteById(long orderId) {
        orderHashOperations.delete(KEY, orderId);
    }

    @Override
    public void deleteAll() {
        orderHashOperations.keys(KEY).stream().forEach(k -> orderHashOperations.delete(KEY, k));
    }
}