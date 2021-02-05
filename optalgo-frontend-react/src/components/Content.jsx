import React, {Component} from 'react'
import LocalSearchInputForm from "./LocalSearchInput/LocalSearchInputForm";
import RectanglesPlacementDisplay from "./RectanglesPlacementDisplay";


class Content extends Component {

  render() {
    const {getRectanglesPlacement, start} = this.props

    return (
      <div className="content-container">
        <LocalSearchInputForm start={start}/>
        <RectanglesPlacementDisplay getRectanglesPlacement={getRectanglesPlacement}/>
      </div>
    )
  }
}

export default Content
