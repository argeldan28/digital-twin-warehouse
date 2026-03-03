import React, { useEffect, useState } from 'react';
import type { InventoryItem } from '../types/warehouse';
import { Package, MapPin } from 'lucide-react';

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
        <div className="flex flex-col h-full overflow-hidden">
            <div className="flex justify-between items-center mb-4 pb-2 border-b border-[var(--color-surface-tertiary)]">
                <h3 className="m-0 text-sm font-semibold text-[var(--color-text-primary)] flex items-center gap-2">
                    <Package size={16} /> Warehouse Inventory
                </h3>
                <span className="badge badge-info">
                    {totalItems} Items Total
                </span>
            </div>

            <div className="flex-1 overflow-y-auto pr-1 flex flex-col gap-2">
                {loading && inventory.length === 0 ? (
                    <div className="text-center text-zinc-500 py-6 text-sm">Loading inventory...</div>
                ) : inventory.length === 0 ? (
                    <div className="text-center text-zinc-500 py-6 text-sm">No items in warehouse.</div>
                ) : (
                    inventory.map((item) => (
                        <div key={item.id} className="bg-[var(--color-surface-secondary)] border border-[var(--color-surface-tertiary)] p-3 rounded-md flex justify-between items-center transition-colors hover:bg-[var(--color-surface-tertiary)]/30">
                            <div className="flex flex-col gap-1">
                                <p className="m-0 font-medium text-[var(--color-text-primary)] text-sm leading-none">{item.product.name}</p>
                                <div className="flex items-center gap-2 text-xs text-[var(--color-text-secondary)] font-mono mt-1">
                                    <span>#{item.product.sku}</span>
                                    <span className="flex items-center gap-1"><MapPin size={10} /> {item.location.x},{item.location.y}</span>
                                </div>
                            </div>
                            <div className="text-right flex items-baseline gap-1">
                                <span className="text-xl font-bold text-brand-base">{item.quantity}</span>
                                <span className="text-xs text-zinc-500 uppercase">pz</span>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default InventoryPanel;
