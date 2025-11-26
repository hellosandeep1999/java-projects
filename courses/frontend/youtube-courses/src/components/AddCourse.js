import axios from 'axios';
import React, { Fragment,useEffect, useState } from 'react'
import { Form,FormGroup, Label,Input, Container, Button } from 'reactstrap';
import base_url from './BaseUrl';
import { toast } from 'react-toastify';

function AddCourse() {
  useEffect(()=>{
      document.title = "Add Course || Code With Sandeep";
    },[])

    const [course, setCourse] = useState({}); 
    const [formSubmitionStatus, setFormSubmitionStatus] = useState('notSubmitted');

   const handleOnSubmit = (e)=>{
    console.log(e);
    postDataOnServer(course);
     e.preventDefault();
   }

   const postDataOnServer = (data)=>{
    axios.post(`${base_url}/course`,data).then(
      (response)=>{
          console.log(response);
          setFormSubmitionStatus('submitted');
          toast.success("Successfully Added");
      },
      (error)=>{
        console.log(error);
        toast.error("Something went wrong");
      }
    )
   }

   const theForm = (
    <>
    <h1 className='text-center my-3'> Fill Course Detail</h1>
    <Form onSubmit={handleOnSubmit}>
        <FormGroup>
            <Label for="courseId">Course Id</Label>
            <Input id="id" name="id" placeholder="Enter Course Id"
            onChange={(e)=>{
              setCourse({...course, id: e.target.value})
            }}/>
        </FormGroup>
        <FormGroup>
            <Label for="title">Course Title</Label>
            <Input type="text" id="title" name="title" placeholder="Enter Course Title"
            onChange={(e)=>{
              setCourse({...course, title: e.target.value})
            }}/>
        </FormGroup>
        <FormGroup>
            <Label for="description">Course Description</Label>
            <Input type="textarea" id="description" name="description" placeholder="Enter Course Description"
            style={{height:120}} onChange={(e)=>{
              setCourse({...course, description: e.target.value})
            }}/>
        </FormGroup>
        <Container>
          <Button type="submit" color='success'>Add Course</Button>
          <Button color='warning mx-2' type='reset'>Clear</Button>
        </Container>
    </Form>
    </>
   );

   const clickForAnother = ()=>{
    setFormSubmitionStatus('notSubmitted');
   }


  return (
    <Fragment>
      {formSubmitionStatus === 'notSubmitted' && theForm}
     {formSubmitionStatus === 'submitted' && (
          <div>
             <h2 color='success'>Thank you for your Submiting! </h2>
             <Button onClick={clickForAnother} color='primary'>Click for Another</Button>
          </div>
        )}
      
    </Fragment>
  )
}

export default AddCourse;
