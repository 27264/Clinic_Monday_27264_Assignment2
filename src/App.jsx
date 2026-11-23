
import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [offices, setOffices] = useState([]);
  const [search, setSearch] = useState("");
  const [error, setError] = useState("");

  // Get all offices from Spring Boot
  useEffect(() => {
    fetch("http://localhost:1014/api/offices/all")
      .then((response) => {
        if (!response.ok) throw new Error("Failed to load offices");
        return response.json();
      })
      .then((data) => setOffices(data))
      .catch(() => setError("Cannot load offices. Check your backend."));
  }, []);

  // Search by office name or office number
  const filteredOffices = offices.filter((office) =>
    office.name.toLowerCase().includes(search.toLowerCase()) ||
    String(office.officeNumber).includes(search)
  );

  // Delete office
  const deleteOffice = async (id) => {
    if (!window.confirm("Are you sure you want to delete this office?")) {
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:1014/api/offices/delete/${id}`,
        { method: "DELETE" }
      );

      if (!response.ok) {
        throw new Error("Delete failed");
      }

      setOffices(offices.filter((office) => office.id !== id));
      setError("");
    } catch {
      setError("Could not delete office.");
    }
  };

  return (
    <div className="container">
      <h1>Office Management</h1>
      <p>Clinic Management System</p>

      <input
        type="text"
        placeholder="Search by office name or number..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      {error && <p style={{ color: "red" }}>{error}</p>}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Office Name</th>
            <th>Office Number</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          {filteredOffices.map((office) => (
            <tr key={office.id}>
              <td>{office.id}</td>
              <td>{office.name}</td>
              <td>{office.officeNumber}</td>
              <td>
                <button
                  className="delete-btn"
                  onClick={() => deleteOffice(office.id)}
                >
                  Delete
                </button>

                <button className="update-btn" disabled>
                  Update
                </button>
              </td>
            </tr>
          ))}

          {filteredOffices.length === 0 && (
            <tr>
              <td colSpan="4">No offices found</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default App;
