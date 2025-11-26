import React from 'react'
import { Link } from 'react-router-dom'
import { ListGroup } from 'reactstrap'

function Menu() {
  return (
    <div>
      <ListGroup>
        <Link className='list-group-item list-group-item-action' tag='a' to="/" action>
            Home
        </Link>
        <Link className='list-group-item list-group-item-action' tag='a' to="/courses" action>
            All Courses
        </Link>
        <Link className='list-group-item list-group-item-action' tag='a' to="/add" action>
            Add Course
        </Link>
        <Link className='list-group-item list-group-item-action' tag='a' to="/about" action>
            About Us
        </Link>
        <Link className='list-group-item list-group-item-action' tag='a' to="/contact" action>
            Contact Us
        </Link>
      </ListGroup>
    </div>
  )
}

export default Menu
