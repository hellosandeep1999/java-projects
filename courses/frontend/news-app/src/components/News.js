
import React, { useEffect, useState } from 'react'
import NewsItem from './NewsItem';
import Spinner from './Spinner';
import InfiniteScroll from 'react-infinite-scroll-component';


function News(props) {

    const [articles, setArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [totalResults, setTotalResults] = useState(0);
    const [page, setPage] = useState(1);

    const updateData = async ()=>{
        props.setProgress(0);
        setLoading(true);
        let url = `https://newsapi.org/v2/top-headlines?country=us&apiKey=${props.api_key}&category=${props.category}&pageSize=${props.pageSize}&page=${page}`
        props.setProgress(20);
        let data = await fetch(url);
        let parsedData = await data.json();
        props.setProgress(70);
        setArticles(parsedData.articles);
        setTotalResults(parsedData.totalResults);
        setLoading(false);
        props.setProgress(100);
    }

     useEffect(()=>  {
        updateData();
        // eslint-disable-next-line
      },[])

    
      const fetchMoreData = async ()=>{
        setLoading(true);
        let url = `https://newsapi.org/v2/top-headlines?country=us&apiKey=${props.api_key}&category=${props.category}&pageSize=${props.pageSize}&page=${page+1}`
        setPage(page+1);
        let data = await fetch(url);
        let parsedData = await data.json();
        setArticles(articles.concat(parsedData.articles));
        setTotalResults(parsedData.totalResults);
        setLoading(false);
      }

  return (
    <>
        
            <h1 className='text-center' style={{ marginTop: '4.5rem' }}> {props.title} News</h1>
            {loading && <Spinner/>}
            <InfiniteScroll
                dataLength={totalResults}
                next={fetchMoreData}
                hasMore={totalResults !== articles.length}
                loader={loading && <Spinner/>}
            >
              <div className='container'>
                {<div className='row'>
                    {articles && articles.map((item) => {
                    return <div className='col-md-4 my-3'>
                        <NewsItem key={item.title+Math.random()} title={item.title} description={item.description} author={item.author} 
                        url={item.url} urlToImage={item.urlToImage}/>
                    </div>
                    })}
                </div>}
              </div>
            </InfiniteScroll>
    </>
  )
}

export default News;
