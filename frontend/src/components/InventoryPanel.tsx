import React, { useEffect, useState } from 'react';
import type { InventoryItem } from '../types/warehouse';

interface InventoryPanelProps {
    fetchInventory: () => Promise<InventoryItem[]>;
    connected: boolean;
}

const InventoryPanel: React.FC<InventoryPanelProps> = ({ fetchInventory, connected }) => {
    const [inventory, setInventory] = useState<InventoryItem[]>([]);
    const [loading, setLoading] = useState(false);

    const loadInventory = async () => {
        if (!connected) return;
        try {
            setLoading(true);
            const data = await fetchInventory();
            setInventory(data);
        } catch (error) {
            console.error("Failed to load inventory", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        let interval: ReturnType<typeof setInterval>;
        if (connected) {
            loadInventory();
            interval = setInterval(loadInventory, 5000); // refresh every 5s
        }
        return () => clearInterval(interval);
    }, [connected]);

    const totalItems = inventory.reduce((acc, item) => acc + item.quantity, 0);

    return (
        <div className="glass-panel" style={{ padding: '24px', flex: 1, overflowY: 'auto', maxHeight: '420px', display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h3 style={{ margin: 0, color: 'var(--slate-700)', fontSize: '1.2rem' }}>Warehouse Inventory</h3>
                <span style={{ fontSize: '12px', fontWeight: 'bold', padding: '4px 8px', backgroundColor: 'var(--primary-100)', color: 'var(--primary-600)', borderRadius: '12px' }}>
                    {totalItems} Items Total
                </span>
            </div>

            {loading && inventory.length === 0 ? (
                <div style={{ textAlign: 'center', color: 'var(--slate-400)', padding: '20px' }}>Loading...</div>
            ) : inventory.length === 0 ? (
                <div style={{ textAlign: 'center', color: 'var(--slate-400)', padding: '20px' }}>No items in inventory.</div>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    {inventory.map((item) => (
                        <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px', backgroundColor: 'var(--slate-50)', border: '1px solid var(--slate-200)', borderRadius: '8px' }}>
                            <div>
                                <p style={{ margin: 0, fontWeight: '600', color: 'var(--slate-700)', fontSize: '14px' }}>{item.product.name}</p>
                                <p style={{ margin: '4px 0 0 0', fontSize: '11px', color: 'var(--slate-500)', fontFamily: 'monospace' }}>SKU: {item.product.sku} | Loc: ({item.location.x}, {item.location.y})</p>
                            </div>
                            <div style={{ textAlign: 'right' }}>
                                <span style={{ fontSize: '18px', fontWeight: 'bold', color: 'var(--primary-600)' }}>{item.quantity}</span>
                                <span style={{ fontSize: '11px', color: 'var(--slate-500)', marginLeft: '4px' }}>pz</span>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default InventoryPanel;
