import React, {Component} from 'react'
import {Group, Layer, Rect, Stage, Text} from 'react-konva'

class Box extends Component {

  boxFillColor = 'white'
  boxBorderColor = 'black'

  rectangleFillColorBase = '#D6E9FE'
  rectangleFillColorUpdated = '#5995DA'
  rectangleBorderColor = 'black'
  rectangleOpacity = 0.7

  getRectangleColor(rectangleId, changedRectangleIds) {
    if (changedRectangleIds.includes(rectangleId)) {
      return this.rectangleFillColorUpdated
    } else {
      return this.rectangleFillColorBase
    }
  }

  render() {
    const {id, unitLength, pixelLength, getRectangles, changedRectangleIds, getShowRectangleIds} = this.props

    function unitToPixel(unit) {
      return unit / unitLength * pixelLength
    }

    function getRectShape(rectangle) {
      return (
        <Group
          x={unitToPixel(rectangle.x)}
          y={unitToPixel(rectangle.y)}
          width={unitToPixel(rectangle.width)}
          height={unitToPixel(rectangle.height)}
        >
          <Rect
            width={unitToPixel(rectangle.width)}
            height={unitToPixel(rectangle.height)}
            fill={this.getRectangleColor(rectangle.id, changedRectangleIds)}
            stroke={this.rectangleBorderColor}
            opacity={this.rectangleOpacity}
          />
          {getShowRectangleIds() ? <Text
            text={rectangle.id}
            fontSize={10}
            padding={3}
          /> : null}
        </Group>
      )
    }

    const rectangles = getRectangles().map(getRectShape.bind(this))

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