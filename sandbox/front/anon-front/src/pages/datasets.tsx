import React, { useEffect, useState } from 'react';
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
                        </div>
                    ))}
                </div>
            )}
            </div>
        </div>
    );
}
