import type { FC } from 'react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';

interface KpiChartProps {
    data: { name: string; value: number }[];
    title: string;
    color: string;
}

const KpiChart: FC<KpiChartProps> = ({ data, title, color }) => {
    return (
        <div className="glass-panel" style={{ padding: '20px', height: '220px', display: 'flex', flexDirection: 'column' }}>
            <h4 style={{ margin: '0 0 15px 0', color: 'var(--slate-600)', fontSize: '14px', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600 }}>{title}</h4>
            <div style={{ flex: 1, minHeight: 0 }}>
                <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={data}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                        <XAxis dataKey="name" hide />
                        <YAxis hide domain={[0, 100]} />
                        <Tooltip />
                        <Area type="monotone" dataKey="value" stroke={color} fill={color} fillOpacity={0.1} />
                    </AreaChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default KpiChart;
