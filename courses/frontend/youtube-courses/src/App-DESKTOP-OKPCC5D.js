import './App.css';
import { ToastContainer, toast } from 'react-toastify';
import Headers from './components/Headers';
import AllCourses from './components/AllCourses';
import {  Col, Row } from 'reactstrap';
import Menu from './components/Menu';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import AddCourse from './components/AddCourse';
import Home from './components/Home';

const toastMessage = ()=>{
  return toast.success("You have successfully Enrolled");
}

function App() {
  return (
    <div className="App">
      <Router>
        <ToastContainer/>
        <Headers/>
        <div className='container'>
          <Row>
            <Col md={4}>
              <Menu/>
            </Col>
            <Col md={8} >
            <Routes>
              <Route key="home" path="/" element={<Home message={toastMessage}/>}  exact></Route>
              <Route key="course" path="/courses" Component={AllCourses} exact></Route>
              <Route key="add" path="/add" Component={AddCourse} exact></Route>
            </Routes>
            </Col>
          </Row>
        </div>
      </Router>
    </div>
  );
}

export default App;
