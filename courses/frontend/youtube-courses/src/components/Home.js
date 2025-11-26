import React, { useEffect } from 'react'
import { Button } from 'reactstrap'

function Home(props) {
  useEffect(()=>{
    document.title = "Home || Code With Sandeep";
  },[])

  return (
    <div className='container my-1 py-5 bg-warning' c>
  
    <h1 className='display-3'>Learn With Sandeep</h1>
    <p className='lead'>This is course hub where we can start the courses in few <br/>clicks and can give a boost to career.</p>
    <Button onClick={props.message} color='primary'> Start Learning</Button>

    </div>
  )
}

export default Home
