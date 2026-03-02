import { NodeTypes } from '../types/warehouse';
import type { Warehouse, NodeType } from '../types/warehouse';
import { motion } from 'framer-motion';

interface WarehouseGridProps {
    warehouse: Warehouse;
}

const WarehouseGrid: React.FC<WarehouseGridProps> = ({ warehouse }) => {
    const cellSize = 30;

    const getNodeColor = (type: NodeType) => {
        switch (type) {
            case NodeTypes.SHELF: return 'var(--slate-500)';
            case NodeTypes.OBSTACLE: return 'var(--slate-800)';
            case NodeTypes.STATION: return 'var(--primary-500)';
            default: return 'var(--slate-50)';
        }
    };

    return (
        <div style={{ position: 'relative', width: '100%', height: '100%', minWidth: warehouse.width * cellSize, minHeight: warehouse.height * cellSize, backgroundColor: 'var(--slate-100)', borderRadius: '12px', overflow: 'hidden', boxShadow: 'inset var(--shadow-md)' }}>
            {/* Render Nodes */}
            {warehouse.grid.map((node, index) => (
                <div
                    key={index}
                    style={{
                        position: 'absolute',
                        left: node.x * cellSize + 1,
                        top: node.y * cellSize + 1,
                        width: cellSize - 2,
                        height: cellSize - 2,
                        backgroundColor: getNodeColor(node.type),
                        borderRadius: node.type !== NodeTypes.WALKABLE ? '4px' : '2px',
                        boxShadow: node.type !== NodeTypes.WALKABLE ? 'var(--shadow-sm)' : 'none',
                        transition: 'background-color 0.3s ease'
                    }}
                />
            ))}

            {/* Render Robots */}
            {warehouse.robots.map((robot) => (
                <motion.div
                    key={robot.id}
                    initial={false}
                    animate={{
                        left: robot.currentNode.x * cellSize,
                        top: robot.currentNode.y * cellSize,
                        scale: robot.state === 'MOVING' ? [1, 1.1, 1] : 1
                    }}
                    transition={{
                        left: { type: 'spring', stiffness: 100, damping: 20 },
                        top: { type: 'spring', stiffness: 100, damping: 20 },
                        scale: { repeat: robot.state === 'MOVING' ? Infinity : 0, duration: 1 }
                    }}
                    style={{
                        position: 'absolute',
                        width: cellSize,
                        height: cellSize,
                        backgroundColor: robot.state === 'ERROR' ? 'var(--danger-bg)' : robot.state === 'MAINTENANCE' ? 'var(--warning-bg)' : 'var(--primary-600)',
                        borderRadius: '50%',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: 'white',
                        fontSize: '14px',
                        zIndex: 10,
                        boxShadow: 'var(--shadow-md)',
                        border: '2px solid white'
                    }}
                >
                    {robot.state === 'ERROR' ? '⚠️' : robot.state === 'MAINTENANCE' ? '🔧' : '⚡'}
                </motion.div>
            ))}
        </div>
    );
};

export default WarehouseGrid;
