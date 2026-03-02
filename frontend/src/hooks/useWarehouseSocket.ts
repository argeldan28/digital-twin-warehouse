import { useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { Warehouse } from '../types/warehouse';

export const useWarehouseSocket = () => {
    const [warehouse, setWarehouse] = useState<Warehouse | null>(null);
    const [connected, setConnected] = useState(false);
    const stompClient = useRef<Client | null>(null);

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws-warehouse'),
            onConnect: () => {
                console.log('Connected to WebSocket');
                setConnected(true);
                client.subscribe('/topic/warehouse/state', (message) => {
                    const state: Warehouse = JSON.parse(message.body);
                    setWarehouse(state);
                });
            },
            onDisconnect: () => {
                console.log('Disconnected from WebSocket');
                setConnected(false);
            },
            debug: (str) => {
                console.log(str);
            },
        });

        client.activate();
        stompClient.current = client;

        return () => {
            client.deactivate();
        };
    }, []);

    const startSimulation = async () => {
        await fetch('http://localhost:8080/api/v1/simulation/start', { method: 'POST' });
    };

    const stopSimulation = async () => {
        await fetch('http://localhost:8080/api/v1/simulation/stop', { method: 'POST' });
    };

    const triggerStressTest = async () => {
        await fetch('http://localhost:8080/api/v1/simulation/scenarios/stress', { method: 'POST' });
    };

    const fetchReplayLogs = async (warehouseId: string) => {
        const response = await fetch(`http://localhost:8080/api/v1/simulation/scenarios/replay/${warehouseId}`);
        return await response.json();
    };

    const fetchInventory = async () => {
        const response = await fetch(`http://localhost:8080/api/v1/inventory`);
        return await response.json();
    };

    return { warehouse, connected, startSimulation, stopSimulation, triggerStressTest, fetchReplayLogs, fetchInventory };
};
