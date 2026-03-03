import { useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { Warehouse } from '../types/warehouse';

export const useWarehouseSocket = (warehouseId: string | null) => {
    const [warehouse, setWarehouse] = useState<Warehouse | null>(null);
    const [connected, setConnected] = useState(false);
    const stompClient = useRef<Client | null>(null);
    const subscriptionRef = useRef<any>(null);

    useEffect(() => {
        const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';
        const client = new Client({
            webSocketFactory: () => new SockJS(`${API_BASE}/ws-warehouse`),
            onConnect: () => {
                console.log('Connected to WebSocket');
                setConnected(true);
            },
            onDisconnect: () => {
                console.log('Disconnected from WebSocket');
                setConnected(false);
            },
            debug: () => {
                // console.log("debug");
            },
        });

        client.activate();
        stompClient.current = client;

        return () => {
            if (subscriptionRef.current) subscriptionRef.current.unsubscribe();
            client.deactivate();
        };
    }, []);

    useEffect(() => {
        if (!connected || !stompClient.current || !warehouseId) return;

        // Unsubscribe from previous
        if (subscriptionRef.current) {
            subscriptionRef.current.unsubscribe();
        }

        subscriptionRef.current = stompClient.current.subscribe(`/topic/warehouse/state/${warehouseId}`, (message) => {
            const state: Warehouse = JSON.parse(message.body);
            setWarehouse(state);
        });

        return () => {
            if (subscriptionRef.current) {
                subscriptionRef.current.unsubscribe();
                subscriptionRef.current = null;
            }
        };
    }, [connected, warehouseId]);

    const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

    const startSimulation = async () => {
        if (!warehouseId) return;
        try { await fetch(`${API_BASE}/api/v1/simulation/start/${warehouseId}`, { method: 'POST' }); }
        catch (e) { console.error("API Error:", e); }
    };

    const stopSimulation = async () => {
        if (!warehouseId) return;
        try { await fetch(`${API_BASE}/api/v1/simulation/stop/${warehouseId}`, { method: 'POST' }); }
        catch (e) { console.error("API Error:", e); }
    };

    const triggerStressTest = async () => {
        if (!warehouseId) return;
        try { await fetch(`${API_BASE}/api/v1/simulation/scenarios/stress/${warehouseId}`, { method: 'POST' }); }
        catch (e) { console.error("API Error:", e); }
    };

    const fetchReplayLogs = async () => {
        if (!warehouseId) return [];
        try {
            const response = await fetch(`${API_BASE}/api/v1/simulation/scenarios/replay/${warehouseId}`);
            return await response.json();
        } catch (e) {
            console.error("API Error:", e);
            return [];
        }
    };

    const fetchInventory = async () => {
        try {
            const response = await fetch(`${API_BASE}/api/v1/inventory`);
            return await response.json();
        } catch (e) {
            console.error("API Error:", e);
            return [];
        }
    };

    return { warehouse, connected, startSimulation, stopSimulation, triggerStressTest, fetchReplayLogs, fetchInventory };
};
