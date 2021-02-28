import React, {Component} from 'react'
import AlgorithmInputForm from "./AlgorithmInput/AlgorithmInputForm";
import RectanglesPlacementDisplay from "./RectanglesPlacementDisplay";


class Content extends Component {

  render() {
    const {getCurrentSolutionStep, getRectanglesLastUpdate, start} = this.props

    return (
      <div className="content-container">
        <AlgorithmInputForm start={start}/>
        <RectanglesPlacementDisplay getCurrentSolutionStep={getCurrentSolutionStep} getRectanglesLastUpdate={getRectanglesLastUpdate}/>
      </div>
    )
  }
}

export default Content
