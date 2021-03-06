import React, {Component} from 'react'
import Header from "./Header";
import Content from "./Content";
import BackendClient from "../client/BackendClient";
import SolutionStepUtil from "../utils/SolutionStepUtil";

class App extends Component {

  fetchSolutionStepsPeriod = 100
  fetchSolutionStepsCount = 100
  visualizationIterationPeriodDefault = 100

  backendClient = new BackendClient()

  state = {
    running: false,
    runId: "",
    fetchBlocked: false,
    solutionStepQueue: [],
    rectanglesLastUpdate: {}
  }

  getCurrentSolutionStep = () => this.state.solutionStepQueue[0]
  getRectanglesLastUpdate = () => this.state.rectanglesLastUpdate

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
        solutionStepQueue: [startSolutionStep.data],
        rectanglesLastUpdate: {}
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
    const lastLoadedStep = last(this.state.solutionStepQueue).step
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
        solutionStepQueue: [
          ...this.state.solutionStepQueue,
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

  removeFirstSolutionStepFromQueue = () => {
    if (this.state.solutionStepQueue.length > 1) {
      const oldSolutionStep = this.state.solutionStepQueue[0]
      const newSolutionStepQueue = this.state.solutionStepQueue.slice(1)
      const newSolutionStep = newSolutionStepQueue[0]
      const newRectanglesLastUpdate = {...this.state.rectanglesLastUpdate}
      this.getUpdatedRectangleIdsInNewStep(oldSolutionStep, newSolutionStep).forEach(id => newRectanglesLastUpdate[id] = newSolutionStep.step)
      this.setState(oldState => ({
        ...oldState,
        solutionStepQueue: newSolutionStepQueue,
        rectanglesLastUpdate: newRectanglesLastUpdate
      }))
    }
  }

  updateRemoveFirstSolutionStepFromQueueInterval(visualizationIterationPeriod) {
    clearInterval(this.removeFirstSolutionStepFromQueueInterval)
    this.removeFirstSolutionStepFromQueueInterval = setInterval(
      this.removeFirstSolutionStepFromQueue,
      visualizationIterationPeriod
    )
  }

  componentDidMount() {
    this.fetchSolutionStepsInterval = setInterval(
      () => {
        if (this.state.running && !this.state.fetchBlocked) {
          this.fetchSolutionSteps()
        }
      },
      this.fetchSolutionStepsPeriod
    )
    this.removeFirstSolutionStepFromQueueInterval = setInterval(
      this.removeFirstSolutionStepFromQueue,
      this.visualizationIterationPeriodDefault
    )
  }

  componentWillUnmount() {
    clearInterval(this.fetchSolutionStepsInterval)
    clearInterval(this.removeFirstSolutionStepFromQueueInterval)
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
          updateVisualizationIterationPeriod={this.updateRemoveFirstSolutionStepFromQueueInterval.bind(this)}
        />
      </div>
    )
  }
}

function last(array) {
  return array[array.length - 1]
}

export default App