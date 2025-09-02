/*import { useState } from 'react'*/
import Logo from './assets/logo.png'
import './App.css'

export function App() {
    /*const [count, setCount] = useState(0)
  */
    return (
        <>
            <header>
                <img id="logo" src={Logo} alt="Logo"></img>
            </header>
            <main>
                <form id="downloadForm" method="GET" action="http://localhost:8080/pozdrow" style={{textAlign: 'center'}}>
                    <div>
                        <select id="choice" name="choice">
                            <option value="1">Option 1 - Normal Person</option>
                            <option value="2">Option 2 - Warehouse Employee</option>
                            <option value="3">Option 3 - Data Warehouse Technician</option>
                            <option value="4">Option 4 - CEO</option>
                            <option value="5">Option 5 - God</option>
                        </select>
                    </div>
                    <div className="switch-label">
                        <label>Use l-diversity?</label>
                        <label className="switch">
                            <input type="checkbox" id="lDivToggle" name="useLDiv"></input>
                            <span className="slider"></span>
                        </label>
                    </div>
                    <button id="submitBtn" name="submit">Submit & Download</button>
                </form>
                <div className="loader" id="loader"></div>
            </main>
        </>
    )
}


