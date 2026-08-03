import './App.css'
import {AuthProvider} from "./context/AuthContext.tsx";
import {BrowserRouter} from "react-router-dom";
import AppRoutes from "./routes/AppRoutes.tsx";

function App() {

  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App
