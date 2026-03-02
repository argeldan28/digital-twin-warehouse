package com.warehouse.digitaltwin.application.services;

import com.warehouse.digitaltwin.domain.model.GridNode;
import com.warehouse.digitaltwin.domain.model.NodeType;
import com.warehouse.digitaltwin.domain.model.Order;
import com.warehouse.digitaltwin.domain.model.OrderItem;
import com.warehouse.digitaltwin.domain.model.OrderState;
import com.warehouse.digitaltwin.domain.model.Warehouse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderGeneratorService {

    private final Random random = new Random();

    public Order generateRandomOrder(Warehouse warehouse) {
        List<GridNode> shelves = warehouse.getGrid().stream()
                .filter(node -> node.getType() == NodeType.SHELF)
                .collect(Collectors.toList());

        if (shelves.isEmpty())
            return null;

        int numItems = random.nextInt(3) + 1;
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            GridNode randomShelf = shelves.get(random.nextInt(shelves.size()));
            OrderItem orderItem = new OrderItem();
            orderItem.setId(UUID.randomUUID());
            orderItem.setLocation(randomShelf);
            items.add(orderItem);
        }

        Order newOrder = new Order();
        newOrder.setId(UUID.randomUUID());
        newOrder.setPriority(random.nextInt(5));
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setSlaTarget(LocalDateTime.now().plusMinutes(30));
        newOrder.setState(OrderState.PENDING);
        newOrder.setItems(items);
        return newOrder;
    }
}
