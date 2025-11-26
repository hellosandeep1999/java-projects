import React from 'react'
import { Link } from 'react-router-dom'

function Navbar() {
  return (
    <div>
      <nav className="navbar fixed-top navbar-expand-lg navbar-dark bg-dark">
        <a className="navbar-brand" href="/">Navbar</a>
        <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
            <ul className="navbar-nav">
            <li className="nav-item active">
                <Link className="nav-link" to="/">Home<span className="sr-only">(current)</span></Link>
            </li>
            <li className="nav-item">
                <Link className="nav-link" to="/business">Business </Link>
            </li>
            <li className="nav-item">
                <Link className="nav-link" to="/entertainment">Entertainment</Link>
            </li>
            <li className="nav-item">
                <Link className="nav-link" to="/general">General</Link>
            </li>
            <li className="nav-item">
                <Link className="nav-link" to="/health">Health</Link>
            </li>
            <li className="nav-item">
                <Link className="nav-link" to="/sciences">Sciences</Link>
            </li>
            <li className="nav-item">
                <Link className="nav-link" to="/ports">Ports</Link>
            </li>
            <li className="nav-item">
                <Link className="nav-link" to="/technology">Technology</Link>
            </li>
            </ul>
        </div>
        </nav>
    </div>
  )
}

export default Navbar
