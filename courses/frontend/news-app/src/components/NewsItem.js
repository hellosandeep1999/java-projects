import React from 'react'

function NewsItem(props) {
  return (
    <div>
        {console.log(props.urlToImage)}
        <div className="card">
            <img className="card-img-top" 
            src={props.urlToImage ? props.urlToImage :"https://static.clubs.nfl.com/image/upload/t_editorial_landscape_12_desktop/ravens/kmhyanrquaja7az3ovmz"} 
            alt=""/>
            <div className="card-body">
                <h5 className="card-title">{props.title}</h5>
                <p className="card-text">{props.description}</p>
                <p className="card-text"><small className="text-muted">Written By {props.author}</small></p>
                <a href={props.url} className="btn btn-primary">Read More</a>
            </div>
        </div>
    </div>
  )
}

export default NewsItem
