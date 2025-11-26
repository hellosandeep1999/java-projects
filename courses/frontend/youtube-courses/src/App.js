import './App.css';
import { ToastContainer, toast } from 'react-toastify';
import Headers from './components/Headers';
import Courses from './components/Courses';
import {  Col, Row } from 'reactstrap';
import Menu from './components/Menu';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

const toastMessage = ()=>{
  return toast.success("You have successfully Enrolled");
}

function App() {
  return (
    <div className="App">
      <Router>
        <ToastContainer/>
        <Headers message={toastMessage}/>
        <div className='container'>
          <Row>
            <Col md={4}>
              <Menu/>
            </Col>
            <Col md={8} >
            <Routes>
              <Route path="/" Component={Headers} exact></Route>
              <Route path="/courses" Component={Courses} exact></Route>
              <Route path="/add" Component={Headers} exact></Route>
              </Routes>
            </Col>
          </Row>
        </div>
      </Router>
    </div>
  );
}

export default App;
