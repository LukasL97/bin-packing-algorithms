import React, {Component} from 'react'
import LocalSearchInputForm from "./LocalSearchInput/LocalSearchInputForm";
import RectanglesPlacementDisplay from "./RectanglesPlacementDisplay";


class Content extends Component {

  render() {
    const {getRectangles, start} = this.props

    return (
      <div className="content-container">
        <LocalSearchInputForm start={start}/>
        <RectanglesPlacementDisplay getRectangles={getRectangles}/>
      </div>
    )
  }
}

export default Content
