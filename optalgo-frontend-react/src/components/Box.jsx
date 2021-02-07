import React, {Component} from 'react'
import {Group, Layer, Rect, Stage} from "react-konva";

class Box extends Component {

  boxFillColor = 'white'
  boxBorderColor = 'black'
  rectangleFillColor = '#D6E9FE'
  rectangleBorderColor = '#5995DA'


  render() {
    const self = this

    const {id, unitLength, pixelLength, getRectangles} = this.props

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
          fill={self.rectangleFillColor}
          stroke={self.rectangleBorderColor}
        />
      )
    }

    const rectangles = getRectangles().map((rectangle, index) => getRectShape(rectangle))

    return (
      <div className="box" id={id}>
        <Stage width={pixelLength} height={pixelLength}>
          <Layer>
            <Rect
              x={0}
              y={0}
              width={pixelLength}
              height={pixelLength}
              fill={this.boxFillColor}
              stroke={this.boxBorderColor}
            />
            <Group x={0} y={0}>
              {rectangles}
            </Group>
          </Layer>
        </Stage>
      </div>
    )
  }
}

export default Box