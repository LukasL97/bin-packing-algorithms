import React, {Component} from 'react'
import Box from "./Box"

class AlgorithmDisplay extends Component {

  getCurrentSolutionStep = this.props.getCurrentSolutionStep
  getRectanglesLastUpdate = this.props.getRectanglesLastUpdate

  state = {
    placement: []
  }

  boxPixelLength = 300

  getRectanglesLastUpdateWithZeroDefault(rectangleId) {
    const lastUpdate = this.getRectanglesLastUpdate()[rectangleId]
    return lastUpdate !== undefined ? lastUpdate : 0
  }

  getRectangles(boxId) {
    return () => this.state.placement
      .filter(placing => placing.box.id === boxId)
      .map(placing => {
        return {
          x: placing.coordinates.x,
          y: placing.coordinates.y,
          width: placing.rectangle.width,
          height: placing.rectangle.height,
          lastUpdate: this.getRectanglesLastUpdateWithZeroDefault(placing.rectangle.id)
        }
      })
  }

  getUnique(boxes) {
    return [...new Set(boxes.map(box => box.id))].map(id => boxes.find(box => box.id === id))
  }

  render() {

    const newSolutionStep = this.getCurrentSolutionStep()

    if (newSolutionStep !== undefined && newSolutionStep.solution.placement !== this.state.placement) {
      this.setState({
        placement: newSolutionStep.solution.placement
      })
      console.log("Visualize solution step " + newSolutionStep.step + " for run with id " + newSolutionStep.runId)
    }

    const boxes = this.getUnique(this.state.placement.map(placing => placing.box))
      .sort((box1, box2) => box1.id - box2.id)
      .map(box => (
        <Box
          id={box.id}
          unitLength={box.length}
          pixelLength={this.boxPixelLength}
          getRectangles={this.getRectangles(box.id)}
          currentStep={newSolutionStep.step}
        />
      ))

    return (
      <div className="algorithm-display">
        <div className="boxes-container">
          {boxes}
        </div>
      </div>
    )
  }
}

export default AlgorithmDisplay