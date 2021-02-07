import React, {Component} from 'react'
import Box from "./Box"

class RectanglesPlacementDisplay extends Component {

  state = {
    placement: []
  }

  boxPixelLength = 300

  getCurrentSolutionStep = this.props.getCurrentSolutionStep

  getRectangles(boxId) {
    return () => this.state.placement
      .filter(placing => placing.box.id === boxId)
      .map(placing => {
        return {
          x: placing.coordinates.x,
          y: placing.coordinates.y,
          width: placing.rectangle.width,
          height: placing.rectangle.height
        }
      })
  }

  getUnique(boxes) {
    return [...new Set(boxes.map(box => box.id))].map(id => boxes.find(box => box.id === id))
  }

  render() {

    const newSolutionStep = this.getCurrentSolutionStep()

    if (typeof newSolutionStep !== 'undefined' && newSolutionStep.solution.placement !== this.state.placement) {
      this.setState({
        placement: newSolutionStep.solution.placement
      })
    }

    const boxes = this.getUnique(this.state.placement.map(placing => placing.box))
      .sort((box1, box2) => box1.id - box2.id)
      .map(box => (
        <Box
          id={box.id}
          unitLength={box.width}
          pixelLength={this.boxPixelLength}
          getRectangles={this.getRectangles(box.id)}
        />
      ))

    return (
      <div className="rectangles-placement-display">
        <div className="boxes-container">
          {boxes}
        </div>
      </div>
    )
  }
}

export default RectanglesPlacementDisplay