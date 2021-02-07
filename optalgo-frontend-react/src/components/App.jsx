import React, {Component} from 'react'
import Header from "./Header";
import Content from "./Content";
import BackendClient from "../client/BackendClient";

class App extends Component {

  fetchSolutionStepsPeriod = 100
  removeFirstSolutionStepFromQueuePeriod = 1000

  backendClient = new BackendClient()

  state = {
    running: false,
    runId: "",
    solutionStepQueue: []
  }

  getCurrentSolutionStep = () => this.state.solutionStepQueue[0]

  start = (
    boxLength,
    numRectangles,
    minWidth,
    maxWidth,
    minHeight,
    maxHeight
  ) => {
    this.backendClient.startRectanglesPlacement(
      'geometryBased',
      boxLength,
      numRectangles,
      minWidth,
      maxWidth,
      minHeight,
      maxHeight
    )(startSolutionStep => {
      console.log(startSolutionStep)
      this.setState({
        running: true,
        runId: startSolutionStep.data.runId,
        solutionStepQueue: [startSolutionStep.data]
      })
    })
  }

  fetchSolutionSteps = () => {
    const lastLoadedStep = last(this.state.solutionStepQueue).step
    this.backendClient.fetchSolutionSteps(
      this.state.runId,
      lastLoadedStep + 1, // TODO: use proper step window
      lastLoadedStep + 10
    )(solutionSteps => {
      console.log(solutionSteps)
      const finished = solutionSteps.data.length > 0 && last(solutionSteps.data).finished
      this.setState({
        running: !finished,
        runId: this.state.runId,
        solutionStepQueue: [
          ...this.state.solutionStepQueue,
          ...solutionSteps.data
        ]
      })
    })
  }

  removeFirstSolutionStepFromQueue = () => {
    if (this.state.solutionStepQueue.length > 1) {
      const newSolutionStepQueue = this.state.solutionStepQueue.slice(1)
      this.setState({
        running: this.state.running,
        runId: this.state.runId,
        solutionStepQueue: newSolutionStepQueue
      })
    }
  }

  componentDidMount() {
    this.fetchSolutionStepsInterval = setInterval(
      () => {
        if (this.state.running) {
          this.fetchSolutionSteps()
        }
      },
      this.fetchSolutionStepsPeriod
    )
    this.removeFirstSolutionStepFromQueueInterval = setInterval(
      this.removeFirstSolutionStepFromQueue,
      this.removeFirstSolutionStepFromQueuePeriod
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
        <Content getCurrentSolutionStep={this.getCurrentSolutionStep} start={this.start}/>
      </div>
    )
  }
}

function last(array) {
  return array[array.length - 1]
}

export default App