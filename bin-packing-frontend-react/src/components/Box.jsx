import React, {Component} from 'react'
import {Group, Layer, Rect, Stage} from "react-konva";
import interpolate from 'color-interpolate'

class Box extends Component {

  boxFillColor = 'white'
  boxBorderColor = 'black'

  rectangleFillColorBase = '#D6E9FE'
  rectangleFillColorLatestUpdate = '#5995DA'
  rectangleBorderColor = '#5995DA'

  rectangleHighlightDuration = 10
  rectangleFillColormap = interpolate([this.rectangleFillColorBase, this.rectangleFillColorLatestUpdate])

  render() {
    const self = this

    const {id, unitLength, pixelLength, getRectangles, currentStep} = this.props

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
          fill={self.rectangleFillColormap(
            Math.max(
              0.0,
              (rectangle.lastUpdate - currentStep + self.rectangleHighlightDuration) / self.rectangleHighlightDuration
            )
          )}
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