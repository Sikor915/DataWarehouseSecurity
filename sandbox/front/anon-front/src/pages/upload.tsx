import React, { useState } from "react";
import Navbar from "../components/navbar";
import "./upload.css";

const Upload: React.FC = () => {
  const [csvFile, setCsvFile] = useState<File | null>(null);
  const [yamlFile, setYamlFile] = useState<File | null>(null);
  const [description, setDescription] = useState<string>("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const token = localStorage.getItem("token");

  const handleFileDrop = (e: React.DragEvent, type: "csv" | "yaml") => {
    e.preventDefault();
    const file = e.dataTransfer.files[0];
    if (!file) return;

    if (type === "csv" && file.name.endsWith(".csv")) setCsvFile(file);
    else if ((type === "yaml" || type === "yml") && (file.name.endsWith(".yaml") || file.name.endsWith(".yml"))) setYamlFile(file);
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>, type: "csv" | "yaml") => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (type === "csv") setCsvFile(file);
    else setYamlFile(file);
  };

  const handleUpload = async () => {
    if (!csvFile || !yamlFile) {
      setMessage("Both CSV and YAML files are required!");
      return;
    }

    setLoading(true);
    const formData = new FormData();
    formData.append("fileData", csvFile);
    formData.append("anonymRules", yamlFile);
    formData.append("filename", csvFile.name);
    formData.append("description", description);

    try {
      const res = await fetch("http://localhost:8080/files/upload", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      if (!res.ok) throw new Error("Upload failed");
      setMessage("Files uploaded successfully!");
      setCsvFile(null);
      setYamlFile(null);
      setDescription("");
    } catch (err) {
      console.error(err);
      setMessage("Error uploading files.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <div className="upload-container">
        <h1>Upload Dataset</h1>
        <div className="upload-card">
          {/* CSV Dropzone */}
          <div
            className={`dropzone ${csvFile ? "selected" : ""}`}
            onDragOver={(e) => e.preventDefault()}
            onDrop={(e) => handleFileDrop(e, "csv")}
          >
            <p>Drag & Drop CSV file here</p>
            <p className="file-name">{csvFile?.name}</p>
            <input
              type="file"
              accept=".csv"
              onChange={(e) => handleFileSelect(e, "csv")}
            />
          </div>

          {/* YAML Dropzone */}
          <div
            className={`dropzone ${yamlFile ? "selected" : ""}`}
            onDragOver={(e) => e.preventDefault()}
            onDrop={(e) => handleFileDrop(e, "yaml")}
          >
            <p>Drag & Drop YAML rules file here</p>
            <p className="file-name">{yamlFile?.name}</p>
            <input
              type="file"
              accept=".yaml,.yml"
              onChange={(e) => handleFileSelect(e, "yaml")}
            />
          </div>

          {/* Description */}
          <textarea
            className="description-input"
            placeholder="Enter description for this dataset..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />

          <button
            className="upload-button"
            onClick={handleUpload}
            disabled={loading}
          >
            {loading ? "Uploading..." : "Upload"}
          </button>
          {message && <p className="message">{message}</p>}
        </div>
      </div>
    </>
  );
};

export default Upload;
