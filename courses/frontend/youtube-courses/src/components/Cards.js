import React from 'react'
import { Card,Button, CardBody, CardSubtitle, CardText } from 'reactstrap'

function Cards(props) {
  return (
    
    <div className='container my-2'>
      <Card>
        <CardBody className='text-center'>
            
            <CardSubtitle className='font-weight-bold'> {props.title}</CardSubtitle>
            <CardText>{props.description}</CardText>
            
            </CardBody>
            <div className='text-center my-2'>
            <Button color="danger">Start</Button>
            <Button color="warning mx-2">Check</Button>
            </div>
           
      
      </Card>
    </div>
  )
}

export default Cards;