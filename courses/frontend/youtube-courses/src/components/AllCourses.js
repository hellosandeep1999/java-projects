import React, { useState,useEffect } from 'react'
import Cards from './Cards'
import axios from 'axios';
import base_url from './BaseUrl';
import { toast } from 'react-toastify';

function AllCourses() {
  

  useEffect(()=>{
    document.title = "All Courses || Code With Sandeep";
    allCoursesFromServer();
  },[]);


  // function to fetch data from server
  const allCoursesFromServer = ()=>{
    axios.get(`${base_url}/courses`).then(
      (response)=>{
          console.log(response);
          toast.success("Courses has been loaded");
          setCourses(response.data);
      },
      (error)=>{
        console.log(error);
        toast.error("Something went wrong");
      }
    )
  }

  const [courses,setCourses] = useState([]);

  const updateCourse = (id)=>{
    setCourses(courses.filter((course)=>course.id !== id))
  }

  return (
    <div>
      {
        courses.length >0 ? courses.map((item)=>
            <Cards title={item.title} description={item.description} id={item.id} update={updateCourse}/>
        ) :"No Courses"
      }
    </div>
  )
}

export default AllCourses;
