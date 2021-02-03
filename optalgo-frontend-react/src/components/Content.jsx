import React, {Component} from 'react'
import LocalSearchInputForm from "./LocalSearchInput/LocalSearchInputForm";
import RectanglesPlacementDisplay from "./RectanglesPlacementDisplay";


class Content extends Component {
  render() {
    return (
      <div className="content-container">
        <LocalSearchInputForm/>
        <RectanglesPlacementDisplay/>
      </div>
    )
  }
}

export default Content
