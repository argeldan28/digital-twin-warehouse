import { useState, useEffect } from 'react';
import { useWarehouseSocket } from './hooks/useWarehouseSocket';
import WarehouseGrid from './components/WarehouseGrid';
import KpiChart from './components/KpiChart';
import InventoryPanel from './components/InventoryPanel';

const App = () => {
  const { warehouse, connected, startSimulation, stopSimulation, triggerStressTest, fetchReplayLogs, fetchInventory } = useWarehouseSocket();
  const [batteryHistory, setBatteryHistory] = useState<{ name: string; value: number }[]>([]);
  const [utilizationHistory, setUtilizationHistory] = useState<{ name: string; value: number }[]>([]);

  useEffect(() => {
    if (warehouse?.kpis) {
      const time = new Date().toLocaleTimeString();
      setBatteryHistory(prev => [...prev.slice(-19), { name: time, value: warehouse.kpis!.averageBatteryLevel }]);
      setUtilizationHistory(prev => [...prev.slice(-19), { name: time, value: warehouse.kpis!.averageRobotUtilization * 100 }]);
    }
  }, [warehouse?.kpis]);

  const injectOrder = async () => {
    await fetch('http://localhost:8080/api/v1/simulation/orders/inject', { method: 'POST' });
  };

  const handleReplay = async () => {
    if (warehouse?.id) {
      try {
        const logs = await fetchReplayLogs(warehouse.id);
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
    <div style={{ padding: '20px', backgroundColor: 'var(--bg-color)', minHeight: '100vh' }}>
      <header style={{ marginBottom: '30px', display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'flex-start', gap: '20px' }}>
        <div>
          <h1 style={{ margin: 0, color: 'var(--slate-800)', fontWeight: 700, letterSpacing: '-0.02em' }}>Digital Twin Warehouse</h1>
          <p style={{ color: 'var(--slate-500)', fontSize: '1.1rem', marginTop: '4px' }}>Simulatore Logistico in Tempo Reale</p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', alignItems: 'flex-end' }}>
          <div className="glass-panel" style={{ padding: '6px 16px', borderRadius: '20px', backgroundColor: connected ? 'var(--success-light)' : 'var(--danger-light)', color: connected ? 'var(--success-dark)' : 'var(--danger-dark)', fontSize: '14px', fontWeight: '600', border: 'none' }}>
            {connected ? '● Server Collegato' : '○ Server Disconnesso'}
          </div>

          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '12px', justifyContent: 'flex-end', marginTop: '8px' }}>
            <div style={{ textAlign: 'center' }}>
              <button onClick={startSimulation} className="btn-modern btn-success" style={{ width: '100%' }}> ▶ Start</button>
              <p style={{ fontSize: '11px', color: 'var(--slate-500)', margin: '6px 0 0 0', maxWidth: '100px', lineHeight: 1.2 }}>Genera la griglia e avvia il motore fisico</p>
            </div>

            <div style={{ textAlign: 'center' }}>
              <button onClick={stopSimulation} className="btn-modern btn-danger" style={{ width: '100%' }}> ⏹ Stop</button>
              <p style={{ fontSize: '11px', color: 'var(--slate-500)', margin: '6px 0 0 0', maxWidth: '100px', lineHeight: 1.2 }}>Arresta la simulazione in corso</p>
            </div>

            <div style={{ textAlign: 'center' }}>
              <button onClick={injectOrder} className="btn-modern btn-primary" style={{ width: '100%' }}> 📦 Inject Order</button>
              <p style={{ fontSize: '11px', color: 'var(--slate-500)', margin: '6px 0 0 0', maxWidth: '100px', lineHeight: 1.2 }}>Crea un nuovo ordine da assegnare ai robot</p>
            </div>

            <div style={{ textAlign: 'center' }}>
              <button onClick={triggerStressTest} className="btn-modern btn-warning" style={{ width: '100%' }}> ⚡ Stress Test</button>
              <p style={{ fontSize: '11px', color: 'var(--slate-500)', margin: '6px 0 0 0', maxWidth: '100px', lineHeight: 1.2 }}>Inietta 50 ordini per testare l'algoritmo A*</p>
            </div>

            <div style={{ textAlign: 'center' }}>
              <button onClick={handleReplay} className="btn-modern" style={{ width: '100%', backgroundColor: 'var(--slate-700)', color: 'white' }}> ⏪ Fetch Replay</button>
              <p style={{ fontSize: '11px', color: 'var(--slate-500)', margin: '6px 0 0 0', maxWidth: '100px', lineHeight: 1.2 }}>Scarica i log storici dal Database Postgres</p>
            </div>
          </div>
        </div>
      </header>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))',
        gap: '20px'
      }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <main className="glass-panel" style={{ padding: '20px', display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '500px', overflowX: 'auto' }}>
            {warehouse ? (
              <WarehouseGrid warehouse={warehouse} />
            ) : (
              <div style={{ textAlign: 'center', color: 'var(--slate-400)' }}>
                <p style={{ fontSize: '48px', margin: '0 0 10px 0' }}>🚧</p>
                <p style={{ fontWeight: 500 }}>Waiting for warehouse state...</p>
              </div>
            )}
          </main>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px' }}>
            <KpiChart data={batteryHistory} title="Avg Fleet Battery (%)" color="#38a169" />
            <KpiChart data={utilizationHistory} title="Robot Utilization (%)" color="#3182ce" />
          </div>
        </div>

        <aside style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div className="glass-panel" style={{ padding: '24px' }}>
            <h3 style={{ marginTop: 0, color: 'var(--slate-700)', fontSize: '1.2rem' }}>KPI Overview</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <div style={{ borderLeft: '4px solid var(--primary-500)', paddingLeft: '12px' }}>
                <p style={{ margin: 0, fontSize: '12px', color: 'var(--slate-500)', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600 }}>Active Robots</p>
                <p style={{ margin: '4px 0 0 0', fontSize: '28px', fontWeight: '700', color: 'var(--slate-800)' }}>{warehouse?.kpis?.activeRobots || 0}</p>
              </div>
              <div style={{ borderLeft: '4px solid var(--success-dark)', paddingLeft: '12px' }}>
                <p style={{ margin: 0, fontSize: '12px', color: 'var(--slate-500)', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600 }}>Orders Pending</p>
                <p style={{ margin: '4px 0 0 0', fontSize: '28px', fontWeight: '700', color: 'var(--slate-800)' }}>{warehouse?.kpis?.pendingOrders || 0}</p>
              </div>
              <div style={{ borderLeft: '4px solid var(--warning-bg)', paddingLeft: '12px' }}>
                <p style={{ margin: 0, fontSize: '12px', color: 'var(--slate-500)', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600 }}>Avg. Utilization</p>
                <p style={{ margin: '4px 0 0 0', fontSize: '28px', fontWeight: '700', color: 'var(--slate-800)' }}>
                  {((warehouse?.kpis?.averageRobotUtilization || 0) * 100).toFixed(1)}%
                </p>
              </div>
            </div>
          </div>

          <div className="glass-panel" style={{ padding: '24px', flex: 1, overflowY: 'auto', maxHeight: '420px' }}>
            <h3 style={{ marginTop: 0, color: 'var(--slate-700)', fontSize: '1.2rem', marginBottom: '16px' }}>Fleet Status</h3>
            {warehouse?.robots.map(robot => (
              <div key={robot.id} style={{ marginBottom: '12px', padding: '12px', backgroundColor: 'var(--slate-50)', border: '1px solid var(--slate-200)', borderRadius: '8px', transition: 'all 0.2s ease' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontWeight: '600', fontSize: '13px', color: 'var(--slate-700)', fontFamily: 'monospace' }}>{robot.id.substring(0, 8)}</span>
                  <span style={{ fontSize: '11px', fontWeight: '600', color: 'var(--slate-500)', backgroundColor: 'var(--slate-200)', padding: '2px 8px', borderRadius: '12px' }}>{robot.state}</span>
                </div>
                <div style={{ marginTop: '10px', width: '100%', height: '6px', backgroundColor: 'var(--slate-200)', borderRadius: '6px', overflow: 'hidden' }}>
                  <div style={{ width: `${robot.batteryLevel}%`, height: '100%', backgroundColor: robot.batteryLevel > 20 ? 'var(--success-bg)' : 'var(--danger-bg)', transition: 'width 0.3s ease' }} />
                </div>
                <div style={{ fontSize: '11px', color: 'var(--slate-400)', marginTop: '6px', display: 'flex', justifyContent: 'space-between' }}>
                  <span>Pos: {robot.currentNode.x},{robot.currentNode.y}</span>
                  <span>{robot.batteryLevel.toFixed(1)}%</span>
                </div>
              </div>
            ))}
          </div>

          <InventoryPanel fetchInventory={fetchInventory} connected={connected} />
        </aside>
      </div>
    </div>
  );
};

export default App;
