import axios from 'axios'
import React from 'react'
import { Card,Button, CardBody, CardSubtitle, CardText } from 'reactstrap'
import base_url from './BaseUrl'
import { toast } from 'react-toastify'

  const deleteCourse = (id)=>{
      axios.delete(`${base_url}/course/${id}`).then(
        (response) =>{
            toast.success("Course Deleted");
        },
        (error) => {
          console.log(error);
          toast.success("Something went wrong");
        }
      )
  };

function Cards(props) {
  return (
    
    <div className='container my-2'>
      <Card>
        <CardBody className='text-center'>
            
            <CardSubtitle className='font-weight-bold'> {props.title}</CardSubtitle>
            <CardText>{props.description}</CardText>
            
            </CardBody>
            <div className='text-center my-2'>
            <Button color="danger" onClick={()=>{deleteCourse(props.id); props.update(props.id)}}>Delete</Button>
            <Button color="warning mx-2">Check</Button>
            </div>
           
      
      </Card>
    </div>
  )
}

export default Cards;