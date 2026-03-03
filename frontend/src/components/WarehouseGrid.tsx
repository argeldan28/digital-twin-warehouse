import React, { useEffect, useRef, useState } from 'react';
import { NodeTypes } from '../types/warehouse';
import type { Warehouse } from '../types/warehouse';
import { Stage, Layer, Rect, Circle, Text, Group } from 'react-konva';

interface WarehouseGridProps {
    warehouse: Warehouse;
    robotNames?: Record<string, string>;
}

const colorMap = {
    [NodeTypes.WALKABLE]: '#09090b', // bg-canvas
    [NodeTypes.SHELF]: '#3f3f46',    // surface-tertiary
    [NodeTypes.STATION]: '#0ea5e9',  // brand-base
    [NodeTypes.OBSTACLE]: '#ef4444', // status-critical
};

const WarehouseGrid: React.FC<WarehouseGridProps> = ({ warehouse, robotNames }) => {
    // Determine cell size dynamically or set statically
    const [containerSize, setContainerSize] = useState({ width: 0, height: 0 });
    const containerRef = useRef<HTMLDivElement>(null);

    // We compute a static cell size, but we let canvas scale or overflow if necessary.
    const cellSize = 30;
    const gridPixelWidth = warehouse.width * cellSize;
    const gridPixelHeight = warehouse.height * cellSize;

    const stageWidth = Math.max(containerSize.width || gridPixelWidth, gridPixelWidth);
    const stageHeight = Math.max(containerSize.height || gridPixelHeight, gridPixelHeight);

    const offsetX = containerSize.width > gridPixelWidth ? (containerSize.width - gridPixelWidth) / 2 : 0;
    const offsetY = containerSize.height > gridPixelHeight ? (containerSize.height - gridPixelHeight) / 2 : 0;

    useEffect(() => {
        if (!containerRef.current) return;
        const resizeObserver = new ResizeObserver(entries => {
            for (let entry of entries) {
                setContainerSize({
                    width: entry.contentRect.width,
                    height: entry.contentRect.height
                });
            }
        });
        resizeObserver.observe(containerRef.current);
        return () => resizeObserver.disconnect();
    }, []);

    // A simple function to interpolate (lerp or animate could be added via Konva's useFrame or similar, 
    // but for now, we just update the explicit X/Y prop bindings. Real-time updates push new positions naturally).

    return (
        <div ref={containerRef} className="w-full h-full min-h-[500px] bg-[var(--color-surface-secondary)] rounded-lg overflow-auto border border-[var(--color-surface-tertiary)] shadow-inner">
            <Stage width={stageWidth} height={stageHeight}>
                {/* Layer 1: The Grid Canvas */}
                <Layer x={offsetX} y={offsetY}>
                    {/* Grid Background Lines (Blueprint style) */}
                    {Array.from({ length: warehouse.width + 1 }).map((_, i) => (
                        <Rect key={`vline-${i}`} x={i * cellSize} y={0} width={1} height={warehouse.height * cellSize} fill="rgba(255,255,255,0.03)" />
                    ))}
                    {Array.from({ length: warehouse.height + 1 }).map((_, i) => (
                        <Rect key={`hline-${i}`} x={0} y={i * cellSize} width={warehouse.width * cellSize} height={1} fill="rgba(255,255,255,0.03)" />
                    ))}

                    {/* Node blocks */}
                    {warehouse.grid.map((node, index) => {
                        if (node.type === NodeTypes.WALKABLE) return null; // Save draw calls for empty space
                        const color = colorMap[node.type as keyof typeof colorMap] || '#3f3f46';
                        return (
                            <Rect
                                key={`node-${index}`}
                                x={node.x * cellSize + 1.5}
                                y={node.y * cellSize + 1.5}
                                width={cellSize - 3}
                                height={cellSize - 3}
                                fill={color}
                                cornerRadius={node.type === NodeTypes.STATION ? 4 : 2}
                                opacity={node.type === NodeTypes.SHELF ? 0.7 : 1}
                            />
                        );
                    })}
                </Layer>

                {/* Layer 2: The Entities & Robots */}
                <Layer x={offsetX} y={offsetY}>
                    {warehouse.robots.map((robot) => {
                        const isError = robot.state === 'ERROR';
                        const isMaintenance = robot.state === 'MAINTENANCE';
                        const bgColor = isError ? '#ef4444' : isMaintenance ? '#f59e0b' : '#38bdf8'; // Accent Cyan for healthy androids
                        const robotNo = robotNames && robotNames[robot.id] ? robotNames[robot.id].replace('Android ', '') : '!';

                        // We use a Group to easily manipulate the unit
                        return (
                            <Group
                                key={`robot-${robot.id}`}
                                x={robot.currentNode.x * cellSize + (cellSize / 2)}
                                y={robot.currentNode.y * cellSize + (cellSize / 2)}
                            >
                                {/* Glow Ring */}
                                {robot.state === 'MOVING' && (
                                    <Circle
                                        radius={(cellSize / 2) + 4}
                                        fill="transparent"
                                        stroke={bgColor}
                                        strokeWidth={1}
                                        opacity={0.4}
                                    />
                                )}

                                {/* Robot Body */}
                                <Circle
                                    radius={(cellSize / 2) - 3}
                                    fill={bgColor}
                                    shadowColor={bgColor}
                                    shadowBlur={8}
                                    shadowOpacity={0.6}
                                />

                                {/* Robot internal text/identifier */}
                                <Text
                                    text={isError ? '⚠️' : isMaintenance ? '🔧' : robotNo}
                                    fontSize={12}
                                    fontFamily="JetBrains Mono, monospace"
                                    fontStyle="bold"
                                    fill={isError || isMaintenance ? 'white' : '#0f172a'}
                                    align="center"
                                    verticalAlign="middle"
                                    offsetX={isError || isMaintenance ? 7 : 4}
                                    offsetY={6}
                                />
                            </Group>
                        );
                    })}
                </Layer>
            </Stage>
        </div>
    );
};

export default WarehouseGrid;
