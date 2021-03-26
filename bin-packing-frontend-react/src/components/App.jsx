import React, {Component} from 'react'
import Header from './Header'
import Content from './Content'
import BackendClient from '../client/BackendClient'
import SolutionStepUtil from '../utils/SolutionStepUtil'

class App extends Component {

  fetchSolutionStepsPeriod = 100
  fetchSolutionStepsCount = 100
  visualizationIterationPeriodDefault = 100

  backendClient = new BackendClient()

  state = {
    running: false,
    runId: '',
    fetchBlocked: false,
    rectanglesLastUpdate: {},
    solutionSteps: [],
    currentStepIndex: 0,
    automaticVisualization: true
  }

  getCurrentSolutionStep = () => this.state.solutionSteps[this.state.currentStepIndex]
  getRectanglesLastUpdate = () => this.state.rectanglesLastUpdate

  getProgress = () => {
    const fetched = last(this.state.solutionSteps)?.step
    const visualized = this.state.currentStepIndex
    const finished = last(this.state.solutionSteps)?.finished
    return {
      fetched: fetched !== undefined ? fetched : 0,
      visualized: visualized !== undefined ? visualized : 0,
      finished: finished !== undefined ? finished : false
    }
  }

  start = (
    strategy,
    boxLength,
    numRectangles,
    minWidth,
    maxWidth,
    minHeight,
    maxHeight
  ) => {
    this.backendClient.startAlgorithm(
      strategy,
      boxLength,
      numRectangles,
      minWidth,
      maxWidth,
      minHeight,
      maxHeight
    )(startSolutionStep => {
      console.log(startSolutionStep)
      this.setState(oldState => ({
        ...oldState,
        running: true,
        runId: startSolutionStep.data.runId,
        rectanglesLastUpdate: {},
        solutionSteps: [startSolutionStep.data],
        currentStepIndex: 0
      }))
    })
  }

  blockFetch = () => {
    this.setState(oldState => ({
      ...oldState,
      fetchBlocked: true
    }))
  }

  fetchSolutionSteps = () => {
    this.blockFetch() // block fetching until fetched data is retrieved via the api and stored in the queue
    const lastLoadedStep = last(this.state.solutionSteps).step
    this.backendClient.fetchSolutionSteps(
      this.state.runId,
      lastLoadedStep + 1,
      lastLoadedStep + this.fetchSolutionStepsCount
    )(solutionSteps => {
      console.log(solutionSteps)
      const finished = solutionSteps.data.length > 0 && last(solutionSteps.data).finished
      this.setState(oldState => ({
        ...oldState,
        running: !finished,
        fetchBlocked: false,
        solutionSteps: [
          ...this.state.solutionSteps,
          ...solutionSteps.data
        ]
      }))
    })
  }

  getUpdatedRectangleIdsInNewStep = (oldSolutionStep, newSolutionStep) => {
    const zippedPlacements = SolutionStepUtil.zipPlacementsByRectangleId(
      oldSolutionStep.solution.placement,
      newSolutionStep.solution.placement
    )
    return zippedPlacements.map(p => {
      if (p.left === null) {
        return p.right.rectangle.id
      } else if (p.right === null || p.left.coordinates.x !== p.right.coordinates.x || p.left.coordinates.y !== p.right.coordinates.y) {
        return p.left.rectangle.id
      } else {
        return null
      }
    }).filter(id => id !== null)
  }

  moveCurrentStepIndex = (index) => {
    if (this.state.solutionSteps.length === 0) {
      return
    }
    let actualIndex = index
    if (index === undefined || index === '' || isNaN(index) || index < 0) {
      actualIndex = 0
    }
    if (index >= this.state.solutionSteps.length) {
      actualIndex = this.state.solutionSteps.length - 1
    }
    const oldSolutionStep = this.state.solutionSteps[this.state.currentStepIndex]
    const newSolutionStep = this.state.solutionSteps[actualIndex]
    const newRectanglesLastUpdate = {...this.state.rectanglesLastUpdate}
    this.getUpdatedRectangleIdsInNewStep(oldSolutionStep, newSolutionStep).forEach(id => newRectanglesLastUpdate[id] = newSolutionStep.step)
    this.setState(oldState => ({
      ...oldState,
      currentStepIndex: actualIndex,
      rectanglesLastUpdate: newRectanglesLastUpdate
    }))
  }

  updateMoveCurrentStepIndexInterval(visualizationIterationPeriod) {
    clearInterval(this.moveCurrentStepIndexInterval)
    this.moveCurrentStepIndexInterval = setInterval(
      () => {
        if (this.state.automaticVisualization) {
          this.moveCurrentStepIndex(this.state.currentStepIndex + 1)
        }
      },
      visualizationIterationPeriod
    )
  }

  toggleAutomaticVisualization(active) {
    this.setState(oldState => ({
      ...oldState,
      automaticVisualization: active
    }))
  }

  getAutomaticVisualization = () => this.state.automaticVisualization
  getCurrentStepIndex = () => this.state.currentStepIndex

  componentDidMount() {
    this.fetchSolutionStepsInterval = setInterval(
      () => {
        if (this.state.running && !this.state.fetchBlocked) {
          this.fetchSolutionSteps()
        }
      },
      this.fetchSolutionStepsPeriod
    )
    this.moveCurrentStepIndexInterval = setInterval(
      () => {
        if (this.state.automaticVisualization) {
          this.moveCurrentStepIndex(this.state.currentStepIndex + 1)
        }
      },
      this.visualizationIterationPeriodDefault
    )
  }

  componentWillUnmount() {
    clearInterval(this.fetchSolutionStepsInterval)
    clearInterval(this.moveCurrentStepIndexInterval)
  }

  render() {
    return (
      <div className="main">
        <Header/>
        <Content
          getCurrentSolutionStep={this.getCurrentSolutionStep}
          getRectanglesLastUpdate={this.getRectanglesLastUpdate}
          start={this.start}
          visualizationIterationPeriodDefault={this.visualizationIterationPeriodDefault}
          updateVisualizationIterationPeriod={this.updateMoveCurrentStepIndexInterval.bind(this)}
          toggleAutomaticVisualization={this.toggleAutomaticVisualization.bind(this)}
          getAutomaticVisualization={this.getAutomaticVisualization.bind(this)}
          getCurrentStepIndex={this.getCurrentStepIndex.bind(this)}
          moveCurrentStepIndex={this.moveCurrentStepIndex.bind(this)}
          getProgress={this.getProgress}
        />
      </div>
    )
  }
}

function last(array) {
  return array[array.length - 1]
}

export default App