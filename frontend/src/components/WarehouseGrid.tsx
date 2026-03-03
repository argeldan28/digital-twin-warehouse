import React from 'react';
import { NodeTypes } from '../types/warehouse';
import type { Warehouse } from '../types/warehouse';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, Html, Box, Sphere, Plane, Edges } from '@react-three/drei';

interface WarehouseGridProps {
    warehouse: Warehouse;
    robotNames?: Record<string, string>;
    isDarkMode: boolean;
}

const colorMap = {
    [NodeTypes.WALKABLE]: '#09090b', // unused directly for rendering blocks
    [NodeTypes.SHELF]: '#3f3f46',    // surface-tertiary
    [NodeTypes.STATION]: '#0ea5e9',  // brand-base
    [NodeTypes.OBSTACLE]: '#ef4444', // status-critical
};

const WarehouseGrid: React.FC<WarehouseGridProps> = ({ warehouse, robotNames, isDarkMode }) => {
    // Determine cell size dynamically or set statically
    const cellSize = 1;
    const gridWidth = warehouse.width;
    const gridHeight = warehouse.height;

    const offsetX = -gridWidth / 2;
    const offsetZ = -gridHeight / 2;

    const getPosition = (x: number, y: number, height: number = 0.5): [number, number, number] => {
        return [
            x * cellSize + offsetX + cellSize / 2,
            height,
            y * cellSize + offsetZ + cellSize / 2
        ];
    };

    // Make Dark Mode floor slightly lighter than pitch black so we can see depth
    const bgColor = isDarkMode ? '#18181b' : '#f4f4f5'; // zinc-900 vs zinc-100
    const gridColor1 = isDarkMode ? '#3f3f46' : '#d4d4d8'; // zinc-700 vs zinc-300
    const gridColor2 = isDarkMode ? '#27272a' : '#e4e4e7'; // zinc-800 vs zinc-200

    return (
        <div className="w-full h-full bg-[var(--color-surface-secondary)] rounded-lg overflow-hidden border border-[var(--color-surface-tertiary)] shadow-inner">
            <Canvas camera={{ position: [0, Math.max(gridWidth, gridHeight) * 1.2, Math.max(gridWidth, gridHeight) * 0.8], fov: 35 }} shadows>
                <color attach="background" args={[bgColor]} />

                <ambientLight intensity={isDarkMode ? 1.5 : 1.2} />
                <directionalLight position={[10, 20, 10]} intensity={isDarkMode ? 2.5 : 1.8} castShadow shadow-mapSize-width={1024} shadow-mapSize-height={1024} />

                <OrbitControls makeDefault minPolarAngle={0} maxPolarAngle={Math.PI / 2 - 0.1} />

                {/* Floor Grid */}
                <gridHelper
                    args={[Math.max(gridWidth, gridHeight) * cellSize, Math.max(gridWidth, gridHeight), gridColor1, gridColor2]}
                    position={[0, 0, 0]}
                />

                {/* Floor Plane */}
                <Plane args={[gridWidth * cellSize, gridHeight * cellSize]} rotation={[-Math.PI / 2, 0, 0]} position={[0, -0.01, 0]} receiveShadow>
                    <meshStandardMaterial color={bgColor} />
                </Plane>

                {/* Node blocks */}
                {warehouse.grid.map((node, index) => {
                    if (node.type === NodeTypes.WALKABLE) return null;

                    const isShelf = node.type === NodeTypes.SHELF;
                    const isStation = node.type === NodeTypes.STATION;
                    const height = isShelf ? 1.5 : isStation ? 0.3 : 1;

                    let color = colorMap[node.type as keyof typeof colorMap] || '#52525b';
                    if (isDarkMode && isShelf) color = '#71717a'; // Pop shelves in Dark Mode

                    // Override dark palette blocks when in light mode
                    if (!isDarkMode && isShelf) color = '#a1a1aa'; // Lighter zinc for shelves
                    if (!isDarkMode && node.type === NodeTypes.OBSTACLE) color = '#ef4444'; // Keep red
                    if (!isDarkMode && isStation) color = '#0284c7'; // Darker blue contrast

                    return (
                        <Box
                            key={`node-${index}`}
                            position={getPosition(node.x, node.y, height / 2)}
                            args={[cellSize * 0.9, height, cellSize * 0.9]}
                            castShadow
                            receiveShadow
                        >
                            <meshStandardMaterial color={color} opacity={isShelf ? 0.9 : 1} transparent={isShelf} />
                            <Edges scale={1.05} threshold={15} color={isDarkMode ? "#18181b" : "#f4f4f5"} />
                        </Box>
                    );
                })}

                {/* Robots */}
                {warehouse.robots.map((robot) => {
                    const isError = robot.state === 'ERROR';
                    const isMaintenance = robot.state === 'MAINTENANCE';
                    const bgColor = isError ? '#ef4444' : isMaintenance ? '#f59e0b' : '#38bdf8'; // Accent Cyan for healthy androids
                    const robotNo = robotNames && robotNames[robot.id] ? robotNames[robot.id].replace('Android ', '') : '!';

                    return (
                        <group key={`robot-${robot.id}`} position={getPosition(robot.currentNode.x, robot.currentNode.y, 0.4)}>
                            {/* Robot Body */}
                            <Sphere args={[cellSize * 0.4, 32, 32]} castShadow>
                                <meshStandardMaterial color={bgColor} emissive={bgColor} emissiveIntensity={isError ? 0.8 : 0.4} />
                            </Sphere>

                            {/* Hover Tag */}
                            <Html position={[0, 0.8, 0]} center style={{ pointerEvents: 'none' }} zIndexRange={[100, 0]}>
                                <div className={`px-2 py-0.5 rounded text-xs font-mono font-bold text-white shadow-lg whitespace-nowrap 
                                    ${isError ? 'bg-red-500' : isMaintenance ? 'bg-amber-500' : isDarkMode ? 'bg-sky-500/80 backdrop-blur' : 'bg-sky-600'}`}>
                                    {isError ? '⚠️' : isMaintenance ? '🔧' : robotNo}
                                </div>
                            </Html>

                            {/* Ring if moving */}
                            {robot.state === 'MOVING' && (
                                <mesh rotation={[-Math.PI / 2, 0, 0]} position={[0, -0.38, 0]}>
                                    <ringGeometry args={[cellSize * 0.5, cellSize * 0.55, 32]} />
                                    <meshBasicMaterial color={bgColor} transparent opacity={0.6} />
                                </mesh>
                            )}
                        </group>
                    );
                })}
            </Canvas>
        </div>
    );
};

export default WarehouseGrid;
