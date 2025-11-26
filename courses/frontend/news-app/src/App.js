import { useState } from 'react'
import './App.css';
import Navbar from './components/Navbar';
import News from './components/News';
import {Route, BrowserRouter as Router, Routes} from 'react-router'
import LoadingBar from 'react-top-loading-bar';




const api_key = process.env.REACT_APP_API_KEY;

const pageSize = 5;

function App() {
  const [progress, setProgress] = useState(0);


  return (
    <>
    <Router>
      <Navbar/>
        <LoadingBar
            color="#f11946"
            height={5}
            progress={progress}
            onLoaderFinished={() => setProgress(0)}
          />
        <Routes>
          <Route exact path='/' element={<News key="generall" setProgress={setProgress} title='Business' api_key={api_key} category='business' pageSize={pageSize}/>} />
          <Route exact path='/business' element={<News key="business"  setProgress={setProgress} title='Business' api_key={api_key} category='business' pageSize={pageSize}/>} />
          <Route exact path='/entertainment' element={<News key="entertainment"  setProgress={setProgress} title='Entertainment' api_key={api_key} category='entertainment' pageSize={pageSize}/>} />
          <Route exact path='/general' element={<News key="general" setProgress={setProgress}  title='General' api_key={api_key} category='general' pageSize={pageSize}/>} />
          <Route exact path='/health' element={<News key="health"  setProgress={setProgress} title='Health' api_key={api_key} category='health' pageSize={pageSize}/>} />
          <Route exact path='/sciences' element={<News key="sciences"  setProgress={setProgress} title='Sciences' api_key={api_key} category='sciences' pageSize={pageSize}/>} />
          <Route exact path='/ports' element={<News key="ports"  setProgress={setProgress} title='Ports' api_key={api_key} category='ports' pageSize={pageSize}/>} />
          <Route exact path='/technology' element={<News key="technology"  setProgress={setProgress} title='Technology' api_key={api_key} category='technology' pageSize={pageSize}/>} />
        </Routes>
    </Router>
    </>
  );
}


export default App;

// hide key  -- Done
// spinner  -- Done
// react router -- 
// infinite scrolling with pagination
// loading bar
// read more button and cut text
// badge

