import React, {Component} from 'react'
import Box from './Box'
import ToggleFormRow from './Input/ToggleFormRow'

class AlgorithmDisplay extends Component {

  getCurrentSolutionStep = this.props.getCurrentSolutionStep
  getShowRectangleIds = this.props.getShowRectangleIds

  state = {
    placement: [],
    update: {jsonClass: 'UnchangedSolution'},
    permutation: null,
    showAllBoxes: false
  }

  maxShownBoxes = 50

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

  getChangedRectangleIds(update) {
    if (update.jsonClass === 'RectanglesChanged') {
      return update.rectangleIds
    } else {
      return []
    }
  }

  handleShowAllBoxesChange(event) {
    this.setState(oldState => ({
      ...oldState,
      showAllBoxes: event.target.checked
    }))
  }

  render() {

    const newSolutionStep = this.getCurrentSolutionStep()

    if (newSolutionStep && newSolutionStep.solution.placement !== this.state.placement) {
      this.setState(oldState => ({
        ...oldState,
        placement: newSolutionStep.solution.placement,
        update: newSolutionStep.solution.update,
        permutation: (newSolutionStep.solution.permutation ? newSolutionStep.solution.permutation : null)
      }))
      console.log('Visualize solution step ' + newSolutionStep.step + ' for run with id ' + newSolutionStep.runId)
    }

    const changedRectangleIds = this.getChangedRectangleIds(this.state.update)

    const numBoxes = this.state.placement.length > 0 ? Math.max(...this.state.placement.map(placing => placing.box.id)) : 0

    const boxes = this.getUnique(this.state.placement.map(placing => placing.box))
      .filter(box => this.state.showAllBoxes || box.id <= this.maxShownBoxes)
      .sort((box1, box2) => box1.id - box2.id)
      .map(box => (
        <Box
          id={box.id}
          unitLength={box.length}
          pixelLength={this.boxPixelLength}
          getRectangles={this.getRectangles(box.id)}
          changedRectangleIds={changedRectangleIds}
          getShowRectangleIds={this.getShowRectangleIds}
        />
      ))

    let permutation = null
    if (this.getShowRectangleIds() && this.state.permutation !== null) {
      permutation = this.state.permutation.map(id => {
        const idChanged = changedRectangleIds.includes(id)
        if (idChanged) {
          return <span style={{color: 'red'}}><strong>{id}</strong></span>
        } else {
          return <span style={{color: 'black'}}>{id}</span>
        }
      }).reduce((prev, curr) => [prev, ', ', curr])
    }

    return (
      <div className="algorithm-display">
        <div>
          {numBoxes > this.maxShownBoxes ?
            <ToggleFormRow
              label={'Show all ' + numBoxes + ' boxes'}
              name={'show-all-boxes'}
              value={false}
              onToggle={this.handleShowAllBoxesChange.bind(this)}
            /> :
            null
          }
        </div>
        <div className="permutation-container">
          <p>
            {permutation}
          </p>
        </div>
        <div className="boxes-container">
          {boxes}
        </div>
      </div>
    )
  }
}

export default AlgorithmDisplay