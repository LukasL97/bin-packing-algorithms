import React, {Component} from 'react'
import Box from "./Box"

class AlgorithmDisplay extends Component {

  getCurrentSolutionStep = this.props.getCurrentSolutionStep
  getShowRectangleIds = this.props.getShowRectangleIds

  state = {
    placement: [],
    update: {jsonClass: "UnchangedSolution"}
  }

  boxPixelLength = 300

  getRectangles(boxId) {
    return () => this.state.placement
      .filter(placing => placing.box.id === boxId)
      .map(placing => {
        return {
          x: placing.coordinates.x,
          y: placing.coordinates.y,
          width: placing.rectangle.width,
          height: placing.rectangle.height,
          id: placing.rectangle.id
        }
      })
  }

  getUnique(boxes) {
    return [...new Set(boxes.map(box => box.id))].map(id => boxes.find(box => box.id === id))
  }

  render() {

    const newSolutionStep = this.getCurrentSolutionStep()

    if (newSolutionStep !== undefined && newSolutionStep.solution.placement !== this.state.placement) {
      this.setState(oldState => ({
        placement: newSolutionStep.solution.placement,
        update: newSolutionStep.solution.update
      }))
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
          update={this.state.update}
          getShowRectangleIds={this.getShowRectangleIds}
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