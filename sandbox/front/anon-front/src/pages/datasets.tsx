import { useEffect, useState } from 'react';
import Navbar from "../components/navbar.tsx";


export default function DatasetsPage() {
    const [tables, setTables] = useState<string[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch('http://localhost:8080/datasets/names' , {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(res => res.json())
            .then((data: { tables: string[] }) => {
                setTables(data.tables);
            })
            .catch(err => console.error(err))
            .finally(() => setLoading(false));
    }, []);

    // Download handler for individual CSV
    const handleDownload = (table: string) => {
        fetch(`http://localhost:8080/datasets/download?name=${encodeURIComponent(table)}`)
            .then(res => res.arrayBuffer())
            .then(buffer => {
                const blob = new Blob([buffer], { type: 'text/csv' });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `${table}.csv`;
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
                <div className="cards-container">
                    {tables.map((table) => (
                        <div key={table} className="card">
                            <h2>{table}</h2>
                            <p>Table name in the database.</p>
                            <button onClick={() => handleDownload(table)}
                                    style={{
                                        padding: "6px 16px",
                                        backgroundColor: "#001f3f",
                                        color: "white",
                                        border: "none",
                                        borderRadius: "6px",
                                        cursor: "pointer",
                                    }}>Download</button>
                        </div>
                    ))}
                </div>
            )}
            </div>
        </div>
    );
}
