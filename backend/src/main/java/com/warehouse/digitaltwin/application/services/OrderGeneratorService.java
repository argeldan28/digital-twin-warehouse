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

        if (shelves.isEmpty()) return null;

        int numItems = random.nextInt(3) + 1;
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            GridNode randomShelf = shelves.get(random.nextInt(shelves.size()));
            items.add(OrderItem.builder()
                    .id(UUID.randomUUID())
                    .location(randomShelf)
                    .build());
        }

        return Order.builder()
                .id(UUID.randomUUID())
                .priority(random.nextInt(5))
                .createdAt(LocalDateTime.now())
                .slaTarget(LocalDateTime.now().plusMinutes(30))
                .state(OrderState.PENDING)
                .items(items)
                .build();
    }
}
