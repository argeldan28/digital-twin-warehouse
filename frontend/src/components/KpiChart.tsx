import type { FC } from 'react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';
import { LineChart as ChartIcon } from 'lucide-react';

interface KpiChartProps {
    data: { name: string; value: number }[];
    title: string;
    color: string;
}

const KpiChart: FC<KpiChartProps> = ({ data, title, color }) => {
    return (
        <div className="panel p-4 h-[220px] flex flex-col">
            <h4 className="m-0 mb-4 text-xs font-semibold text-[var(--color-text-primary)] flex items-center gap-2 uppercase tracking-wider">
                <ChartIcon size={14} /> {title}
            </h4>
            <div className="flex-1 min-h-0">
                <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={data}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(255,255,255,0.05)" />
                        <XAxis dataKey="name" hide />
                        <YAxis hide domain={[0, 100]} />
                        <Tooltip
                            contentStyle={{ backgroundColor: '#18181b', borderColor: '#3f3f46', borderRadius: '6px', color: '#f4f4f5', fontSize: '12px' }}
                            itemStyle={{ color: color }}
                        />
                        <Area type="monotone" dataKey="value" stroke={color} strokeWidth={2} fill={color} fillOpacity={0.15} />
                    </AreaChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default KpiChart;
