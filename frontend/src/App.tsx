import { useState, useEffect } from 'react';
import { useWarehouseSocket } from './hooks/useWarehouseSocket';
import WarehouseGrid from './components/WarehouseGrid';
import KpiChart from './components/KpiChart';
import InventoryPanel from './components/InventoryPanel';
import { Play, Square, Package, Zap, RotateCcw, Server, Cpu, Navigation, Battery, Activity, Moon, Sun } from 'lucide-react';

const App = () => {
  const [activeWarehouses, setActiveWarehouses] = useState<any[]>([]);
  const [selectedWarehouseId, setSelectedWarehouseId] = useState<string | null>(null);

  const { warehouse, connected, startSimulation, stopSimulation, triggerStressTest, fetchReplayLogs, fetchInventory } = useWarehouseSocket(selectedWarehouseId);
  const [batteryHistory, setBatteryHistory] = useState<{ name: string; value: number }[]>([]);
  const [utilizationHistory, setUtilizationHistory] = useState<{ name: string; value: number }[]>([]);
  const [robotNames, setRobotNames] = useState<Record<string, string>>({});
  const [isDarkMode, setIsDarkMode] = useState(true);

  // Poll for existing warehouses
  useEffect(() => {
    const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    const fetchWarehouses = async () => {
      try {
        const res = await fetch(`${API_BASE}/api/v1/simulation/warehouses`);
        const data = await res.json();
        setActiveWarehouses(data);
        if (data.length > 0 && !selectedWarehouseId) {
          setSelectedWarehouseId(data[0].id);
        }
      } catch (e) {
        console.error("Failed to fetch warehouses", e);
      }
    };
    fetchWarehouses();
    const interval = setInterval(fetchWarehouses, 5000);
    return () => clearInterval(interval);
  }, [selectedWarehouseId]);

  const spawnNewWarehouse = async () => {
    const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    try {
      const res = await fetch(`${API_BASE}/api/v1/simulation/warehouses/spawn`, { method: 'POST' });
      const newW = await res.json();
      setSelectedWarehouseId(newW.id);
    } catch (e) {
      console.error(e);
    }
  };

  const injectOrder = async () => {
    const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    if (selectedWarehouseId) {
      try {
        await fetch(`${API_BASE}/api/v1/simulation/orders/inject/${selectedWarehouseId}`, { method: 'POST' });
      } catch (e) { console.error("API Error", e); }
    }
  };

  const toggleTheme = () => {
    setIsDarkMode(!isDarkMode);
    if (isDarkMode) {
      document.body.classList.add('theme-light');
    } else {
      document.body.classList.remove('theme-light');
    }
  };

  useEffect(() => {
    if (warehouse?.robots) {
      setRobotNames(prev => {
        const newNames = { ...prev };
        let count = Object.keys(prev).length + 1;
        let changed = false;
        warehouse.robots.forEach(robot => {
          if (!newNames[robot.id]) {
            newNames[robot.id] = `Android ${count++}`;
            changed = true;
          }
        });
        return changed ? newNames : prev;
      });
    }
  }, [warehouse?.robots]);

  useEffect(() => {
    if (warehouse?.robots) {
      const time = new Date().toLocaleTimeString();

      const avgBattery = warehouse.robots.length
        ? warehouse.robots.reduce((acc, r) => acc + r.batteryLevel, 0) / warehouse.robots.length
        : 0;

      const utilization = (warehouse.kpis as any)?.averageSlaCompliance || 0;

      setBatteryHistory(prev => [...prev.slice(-19), { name: time, value: avgBattery }]);
      setUtilizationHistory(prev => [...prev.slice(-19), { name: time, value: utilization * 100 }]);
    }
  }, [warehouse]);

  const handleReplay = async () => {
    if (selectedWarehouseId) {
      try {
        const logs = await fetchReplayLogs();
        alert(`Fetched ${logs.length} replay event logs for warehouse!`);
        console.log(logs);
      } catch (error) {
        console.error("Failed to fetch replay logs", error);
        alert("Failed to fetch replay logs");
      }
    } else {
      alert("No active warehouse session to replay.");
    }
  };

  return (
    <div className="p-4 md:p-6 max-w-[1920px] mx-auto">
      <header className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6 mb-8">
        <div className="flex items-center gap-4">
          <img src="/logo-warehouse.png" alt="Warehouse Logo" className="w-[50px] h-[50px] md:w-[60px] md:h-[60px] object-contain drop-shadow-[0_0_15px_rgba(14,165,233,0.3)]" />
          <div>
            <h1 className="text-xl md:text-2xl font-bold text-[var(--color-text-primary)] tracking-tight m-0">Digital Twin Warehouse</h1>
            <div className="flex items-center gap-2 mt-1">
              <select
                value={selectedWarehouseId || ''}
                onChange={(e) => setSelectedWarehouseId(e.target.value)}
                className="bg-[var(--color-surface-secondary)] text-[var(--color-text-primary)] text-sm md:text-base border border-[var(--color-surface-tertiary)] rounded px-2 py-1 outline-none"
              >
                {activeWarehouses.length === 0 && <option value="">No Facilities Active...</option>}
                {activeWarehouses.map((w, idx) => (
                  <option key={w.id} value={w.id}>
                    Facility {idx + 1} ({w.id.split('-')[0]})
                  </option>
                ))}
              </select>
              <button
                onClick={spawnNewWarehouse}
                className="text-xs font-semibold px-2 py-1 bg-indigo-500/10 text-indigo-400 hover:bg-indigo-500/20 rounded border border-indigo-500/30 transition-colors"
                title="Spawn a new random warehouse facility instance"
              >
                + New Facility
              </button>
            </div>
          </div>
        </div>

        <div className="flex flex-col items-start md:items-end gap-3 w-full md:w-auto">
          <div className="flex flex-row items-center gap-3">
            <button onClick={toggleTheme} className="p-2 rounded-full hover:bg-[var(--color-surface-secondary)] text-[var(--color-text-primary)] border border-[var(--color-surface-tertiary)] transition-colors" title="Toggle Theme">
              {isDarkMode ? <Sun size={14} /> : <Moon size={14} />}
            </button>
            <div className={`badge ${connected ? 'badge-success' : 'badge-critical'} flex gap-2 items-center`}>
              <Server size={14} />
              {connected ? 'SYSTEM ONLINE' : 'SYSTEM OFFLINE'}
            </div>
          </div>

          <div className="grid grid-cols-2 lg:flex lg:flex-row gap-2 w-full">
            <button onClick={startSimulation} className="btn btn-success" title="Start Physics Engine">
              <Play size={16} /> <span className="hidden sm:inline">Start</span>
            </button>
            <button onClick={stopSimulation} className="btn btn-danger" title="Halt Simulation">
              <Square size={16} /> <span className="hidden sm:inline">Stop</span>
            </button>
            <button onClick={injectOrder} className="btn btn-primary" title="Inject Order Task">
              <Package size={16} /> <span className="hidden sm:inline">Inject Order</span>
            </button>
            <button onClick={triggerStressTest} className="btn btn-warning" title="Spawn 50 Orders">
              <Zap size={16} /> <span className="hidden sm:inline">Stress Test</span>
            </button>
            <button onClick={handleReplay} className="btn btn-secondary col-span-2 lg:col-span-1" title="Fetch Database Logs">
              <RotateCcw size={16} /> <span className="lg:hidden">Fetch DB Logs</span>
            </button>
          </div>
        </div>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-[2.5fr_1fr] gap-6 items-start">
        {/* Left Column: Map & Charts */}
        <div className="flex flex-col gap-6">
          <main className="panel p-0 flex justify-center items-center overflow-hidden min-h-[500px]">
            {warehouse ? (
              <WarehouseGrid warehouse={warehouse} robotNames={robotNames} />
            ) : (
              <div className="text-center text-zinc-500 py-20 flex flex-col items-center gap-4">
                <Server size={48} className="animate-pulse opacity-50" />
                <p className="font-medium">Waiting for telemetry stream...</p>
              </div>
            )}
          </main>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <KpiChart data={batteryHistory} title="Avg Fleet Battery (%)" color="#10b981" />
            <KpiChart data={utilizationHistory} title="Robot Utilization (%)" color="#0ea5e9" />
          </div>
        </div>

        {/* Right Column: KPIs & Fleet Status */}
        <aside className="flex flex-col gap-6 h-full">
          <div className="grid grid-cols-2 gap-4">
            <div className="kpi-card">
              <span className="kpi-title"><Cpu size={14} /> Active Units</span>
              <span className="kpi-value">
                {warehouse?.robots?.length || 0}
              </span>
            </div>
            <div className="kpi-card">
              <span className="kpi-title uppercase"><Package size={14} /> Pending</span>
              <span className="kpi-value text-[var(--color-status-success)]">
                {warehouse?.activeOrders ? warehouse.activeOrders.filter((o: any) => o.state === 'PENDING').length : 0}
              </span>
            </div>
            <div className="kpi-card col-span-2">
              <span className="kpi-title uppercase"><Activity size={14} /> Avg. Utilization</span>
              <span className="kpi-value text-[var(--color-status-warning)]">
                {(((warehouse?.kpis as any)?.averageSlaCompliance || 0) * 100).toFixed(1)}%
              </span>
            </div>
          </div>

          <div className="panel flex flex-col max-h-[480px]">
            <div className="p-4 border-b border-[var(--color-surface-tertiary)] flex justify-between items-center bg-[var(--color-surface-primary)] rounded-t-lg sticky top-0 z-10">
              <h3 className="m-0 text-sm font-semibold text-[var(--color-text-primary)] flex items-center gap-2"><Navigation size={16} /> Fleet Telemetry</h3>
              <span className="badge badge-info">{warehouse?.robots.length || 0} Units</span>
            </div>

            <div className="p-4 overflow-y-auto flex flex-col gap-3">
              {warehouse?.robots.map(robot => (
                <div key={robot.id} className="bg-[var(--color-surface-secondary)] border border-[var(--color-surface-tertiary)] p-3 rounded-md flex flex-col gap-2 transition-all hover:bg-[var(--color-surface-tertiary)]/50">
                  <div className="flex justify-between items-center">
                    <span className="font-mono font-bold text-sm text-[var(--color-text-primary)]">{robotNames[robot.id] || `Unit`}</span>
                    <span className={`badge ${robot.state === 'ERROR' ? 'badge-critical' : robot.state === 'MAINTENANCE' ? 'badge-warning' : robot.state === 'IDLE' ? 'badge-info' : 'badge-success'}`}>
                      {robot.state}
                    </span>
                  </div>

                  <div className="w-full h-1.5 bg-zinc-800 rounded-full overflow-hidden">
                    <div
                      className={`h-full transition-all duration-300 ${robot.batteryLevel > 20 ? 'bg-emerald-500' : 'bg-red-500'}`}
                      style={{ width: `${robot.batteryLevel}%` }}
                    />
                  </div>

                  <div className="flex justify-between text-xs text-zinc-400 font-mono">
                    <span className="flex items-center gap-1"><Navigation size={12} /> {robot.currentNode.x}, {robot.currentNode.y}</span>
                    <span className="flex items-center gap-1"><Battery size={12} /> {robot.batteryLevel.toFixed(1)}%</span>
                  </div>
                </div>
              ))}
              {(!warehouse?.robots || warehouse.robots.length === 0) && (
                <p className="text-sm text-zinc-500 text-center py-4">No active robots in sector.</p>
              )}
            </div>
          </div>

          <div className="panel p-4">
            <InventoryPanel fetchInventory={fetchInventory} connected={connected} />
          </div>
        </aside>
      </div>
    </div>
  );
};

export default App;
