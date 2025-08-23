<%-- 
    Document   : index
    Created on : Aug 23, 2025, 1:23:49 PM
    Author     : sikor
--%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>K-Anonymity and L-Diversity tester</title>
        <style>
            body {
                margin: 0;
                background-color: #001f3f;
                color: #fff;
                font-family: Arial, sans-serif;
                min-height: 100vh;
                display: flex;
                flex-direction: column;
            }
            header {
                padding: 10px;
            }
            #logo {
                height: 150px;
            }
            main {
                flex: 1;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
            }
            select, .switch-label, button {
                margin: 10px 0;
                font-size: 16px;
            }
            button {
                padding: 10px 20px;
                cursor: pointer;
            }
            .loader {
                border: 8px solid #ccc;
                border-top: 8px solid #27ae60;
                border-radius: 50%;
                width: 60px;
                height: 60px;
                margin: 20px auto;
                animation: spin 1s linear infinite;
                display: none;
            }
            @keyframes spin {
                0% {
                    transform: rotate(0deg);
                }
                100% {
                    transform: rotate(360deg);
                }
            }
            .switch {
                position: relative;
                display: inline-block;
                width: 60px;
                height: 34px;
                margin-left: 10px;
            }
            .switch input {
                opacity: 0;
                width: 0;
                height: 0;
            }
            .slider {
                position: absolute;
                cursor: pointer;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background-color: #ccc;
                transition: .4s;
                border-radius: 34px;
            }
            .slider:before {
                position: absolute;
                content: "";
                height: 26px;
                width: 26px;
                left: 4px;
                bottom: 4px;
                background-color: white;
                transition: .4s;
                border-radius: 50%;
            }
            input:checked + .slider {
                background-color: #2196F3;
            }
            input:checked + .slider:before {
                transform: translateX(26px);
            }
            footer {
                text-align: center;
                padding: 10px;
                background-color: #001a36;
                font-size: 14px;
            }
        </style>
    </head>
    <body>
        <header><img id="logo" src="logo.png" alt="Logo"></header>
        <main>
            <form id="downloadForm" method="POST" action="process" style="text-align: center">
                <div>
                    <select id="choice" name="choice">
                        <option value="1">Option 1 - Normal Person</option>
                        <option value="2">Option 2 - Warehouse Employee</option>
                        <option value="3">Option 3 - Data Warehouse Technician</option>
                        <option value="4">Option 4 - CEO </option>
                        <option value="5">Option 5 - God </option>
                    </select>
                </div>
                <div class="switch-label">
                    <label>Use l-diversity?</label>
                    <label class="switch">
                        <input type="checkbox" id="lDivToggle" name="useLDiv">
                        <span class="slider"></span>
                    </label>
                </div>
                <button id="submitBtn" name="submit">Submit & Download</button>
            </form>
            <div class="loader" id="loader"></div>
        </main>
        <footer>© 2025 Kacper Sikorski & Mateusz Falfus. Project made for HDiSED at SUT</footer>

        <script>
            const form = document.getElementById('downloadForm');
            const loader = document.getElementById('loader');

            form.addEventListener('submit', () => {
                loader.style.display = 'block';
            });
        </script>
    </body>
</html>


