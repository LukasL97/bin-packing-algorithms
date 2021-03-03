import React, {Component} from 'react'
import AlgorithmInputForm from "./AlgorithmInput/AlgorithmInputForm";
import AlgorithmDisplay from "./AlgorithmDisplay";


class Content extends Component {

  render() {
    const {getCurrentSolutionStep, getRectanglesLastUpdate, start} = this.props

    return (
      <div className="content-container">
        <AlgorithmInputForm start={start}/>
        <AlgorithmDisplay getCurrentSolutionStep={getCurrentSolutionStep} getRectanglesLastUpdate={getRectanglesLastUpdate}/>
      </div>
    )
  }
}

export default Content
