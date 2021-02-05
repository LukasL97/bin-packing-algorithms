import React, {Component} from 'react'
import Box from "./Box"

class RectanglesPlacementDisplay extends Component {

  boxPixelLength = 300

  getRectanglesPlacement = this.props.getRectanglesPlacement

  getRectangles(boxId) {
    return () => this.getRectanglesPlacement()
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

  render() {

    const boxes = [...new Set(this.getRectanglesPlacement().map(placing => placing.box))]
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