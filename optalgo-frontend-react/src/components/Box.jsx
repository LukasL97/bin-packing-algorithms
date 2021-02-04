import React, {Component} from 'react'
import {Group, Layer, Rect, Stage} from "react-konva";

class Box extends Component {

  boxFillColor = 'white'
  boxBorderColor = 'black'
  rectangleColor = 'green'

  render() {
    const self = this

    const {unitLength, pixelLength, getRectangles} = this.props

    function unitToPixel(unit) {
      return unit / unitLength * pixelLength
    }

    function getRectShape(rectangle) {
      return (
        <Rect
          x={unitToPixel(rectangle.x)}
          y={unitToPixel(rectangle.y)}
          width={unitToPixel(rectangle.width)}
          height={unitToPixel(rectangle.height)}
          fill={self.rectangleColor}
        />
      )
    }

    const rectangles = getRectangles().map((rectangle, index) => getRectShape(rectangle))

    return (
      <Stage width={pixelLength} height={pixelLength}>
        <Layer>
          <Rect x={0} y={0} width={pixelLength} height={pixelLength} fill={this.boxFillColor} stroke={this.boxBorderColor}/>
          <Group x={0} y={0}>
            {rectangles}
          </Group>
        </Layer>
      </Stage>
    )
  }
}

export default Box