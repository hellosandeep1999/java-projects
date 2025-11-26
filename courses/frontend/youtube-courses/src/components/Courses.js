import React, { useState } from 'react'
import Cards from './Cards'

function Courses() {
    const [courses,setCourses] = useState([
        {title : "Java Courses", description: "This is demo Courses"},
        {title : "Python Courses", description: "This is demo Courses"},
        {title : "Angular Courses", description: "This is demo Courses"}
     ])
  return (
    <div>
      {
        courses.length >0 ? courses.map((item)=>
            <Cards title={item.title} description={item.description} />
        ) :"No Courses"
      }
    </div>
  )
}

export default Courses
