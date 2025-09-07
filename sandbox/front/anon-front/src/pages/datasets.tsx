import { useEffect, useState } from 'react';
import Navbar from "../components/navbar.tsx";

const TRUST_LEVELS = ["Novice", "Learner", "Contributor", "Trusted", "Admin"] as const;

// map string trust levels to ints
const TRUST_MAP: Record<typeof TRUST_LEVELS[number], number> = {
    Novice: 1,
    Learner: 2,
    Contributor: 3,
    Trusted: 4,
    Admin: 5,
};

export default function DatasetsPage() {
    const [tables, setTables] = useState<string[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch('http://localhost:8080/datasets/names')
            .then(res => res.json())
            .then((data: { tables: string[] }) => setTables(data.tables))
            .catch(err => console.error(err))
            .finally(() => setLoading(false));
    }, []);

    const handleDownload = (table: string, trustLevel: typeof TRUST_LEVELS[number]) => {
        const trustInt = TRUST_MAP[trustLevel]; // convert string → int

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
                    tables.map((table) => (
                        <div key={table}>
                            <h2>{table}</h2>
                            <div className="cards-container">
                                {TRUST_LEVELS.map((level) => (
                                    <div key={level} className="card">
                                        <h3 style={{color: "black"}}>{level}</h3>
                                        <button
                                            onClick={() => handleDownload(table, level)}
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
