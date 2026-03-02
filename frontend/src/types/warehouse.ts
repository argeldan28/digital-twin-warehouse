export type RobotState = 'IDLE' | 'MOVING' | 'CHARGING' | 'ERROR' | 'MAINTENANCE';

export type OrderState = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';

export type NodeType = 'WALKABLE' | 'OBSTACLE' | 'SHELF' | 'STATION';

export const NodeTypes = {
    WALKABLE: 'WALKABLE' as NodeType,
    OBSTACLE: 'OBSTACLE' as NodeType,
    SHELF: 'SHELF' as NodeType,
    STATION: 'STATION' as NodeType,
};

export interface Product {
    id: string;
    sku: string;
    name: string;
    category: string;
}

export interface InventoryItem {
    id: string;
    product: Product;
    quantity: number;
    location: GridNode;
}

export interface GridNode {
    x: number;
    y: number;
    type: NodeType;
    inventory?: InventoryItem;
}

export interface Robot {
    id: string;
    currentNode: GridNode;
    batteryLevel: number;
    state: RobotState;
    speed: number;
}

export interface Order {
    id: string;
    priority: number;
    state: OrderState;
}

export interface WarehouseKpis {
    totalOrdersCompleted: number;
    throughputPerHour: number;
    averageRobotUtilization: number;
    averageBatteryLevel: number;
    activeRobots: number;
    pendingOrders: number;
}

export interface Warehouse {
    id: string;
    width: number;
    height: number;
    grid: GridNode[];
    robots: Robot[];
    activeOrders: Order[];
    kpis?: WarehouseKpis;
}
