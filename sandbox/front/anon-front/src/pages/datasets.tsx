import { useEffect, useState } from 'react';
import Navbar from "../components/navbar.tsx";

const TRUST_LEVELS = ["Novice", "Learner", "Contributor", "Trusted", "Admin"] as const;

const TRUST_MAP: Record<typeof TRUST_LEVELS[number], number> = {
    Novice: 1,
    Learner: 2,
    Contributor: 3,
    Trusted: 4,
    Admin: 5,
};

interface Dataset {
    name: string;
    description: string | null;
}

export default function DatasetsPage() {
    const [datasets, setDatasets] = useState<Dataset[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch('http://localhost:8080/datasets/names')
            .then(res => res.json())
            .then((data: any[]) => {
                if (Array.isArray(data)) {
                    const mapped = data.map(item => ({
                        name: item.first,
                        description: item.second
                    }));
                    setDatasets(mapped);
                } else {
                    console.error('Expected array but got:', typeof data, data);
                    setDatasets([]);
                }
            })
            .catch(err => console.error(err))
            .finally(() => setLoading(false));
    }, []);

    const handleDownload = (table: string, trustLevel: typeof TRUST_LEVELS[number]) => {
        const trustInt = TRUST_MAP[trustLevel];

        fetch(`http://localhost:8080/datasets/download?name=${encodeURIComponent(table)}&trust=${trustInt}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token') || ''}`,
            }
        })
            .then(res => {
                if (!res.ok) throw new Error(`HTTP ${res.status}`);
                return res.arrayBuffer();
            })
            .then(buffer => {
                const blob = new Blob([buffer], { type: 'text/csv' });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `${table}_${trustLevel}.csv`;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            })
            .catch(err => console.error('Download error', err));
    };

    return (
        <div>
            <Navbar />
            <div className="home-container">
                <h1>Available Datasets</h1>
                {loading ? (
                    <p>Loading…</p>
                ) : (
                    datasets.map((dataset) => (
                        <div key={dataset.name}>
                            <h2>{dataset.name}</h2>
                            {dataset.description && <p>{dataset.description}</p>}
                            <div className="cards-container">
                                {TRUST_LEVELS.map((level) => (
                                    <div key={`${dataset.name}-${level}`} className="card">
                                        <h3 style={{color: "black"}}>{level}</h3>
                                        <button
                                            onClick={() => handleDownload(dataset.name, level)}
                                            style={{
                                                padding: "6px 16px",
                                                backgroundColor: "#001f3f",
                                                color: "white",
                                                border: "none",
                                                borderRadius: "6px",
                                                cursor: "pointer",
                                            }}
                                        >
                                            Download
                                        </button>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}