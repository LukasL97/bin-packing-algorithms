import React, {Component} from 'react'
import Box from "./Box";

class RectanglesPlacementDisplay extends Component {
  render() {
    const {getRectangles} = this.props

    return (
      <div className="rectangles-placement-display">
        <div className="boxes-container">
          <Box unitLength={100} pixelLength={300} getRectangles={getRectangles}/>
          <Box unitLength={100} pixelLength={300} getRectangles={getRectangles}/>
          <Box unitLength={100} pixelLength={300} getRectangles={getRectangles}/>
          <Box unitLength={100} pixelLength={300} getRectangles={getRectangles}/>
          <Box unitLength={100} pixelLength={300} getRectangles={getRectangles}/>
          <Box unitLength={100} pixelLength={300} getRectangles={getRectangles}/>
          <Box unitLength={100} pixelLength={300} getRectangles={getRectangles}/>
          <Box unitLength={100} pixelLength={300} getRectangles={getRectangles}/>
        </div>
      </div>
    )
  }
}

export default RectanglesPlacementDisplay