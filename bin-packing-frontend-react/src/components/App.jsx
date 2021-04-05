import React, {Component} from 'react'
import Header from './Header'
import Content from './Content'
import BackendClient from '../client/BackendClient'

class App extends Component {

  fetchSolutionStepsPeriod = 100
  fetchSolutionStepsCount = 100
  fetchCombinedSolutionStepsCount = 10000
  visualizationIterationPeriodDefault = 100

  backendClient = new BackendClient()

  state = {
    running: false,
    runId: '',
    fetchBlocked: false,
    solutionSteps: [],
    currentStepIndex: 0,
    automaticVisualization: true,
    combineSteps: false,
    showRectangleIds: false
  }

  getCurrentSolutionStep = () => this.state.solutionSteps[this.state.currentStepIndex]

  getProgress = () => {
    const fetched = this.state.solutionSteps.length - 1
    const visualized = this.state.currentStepIndex
    const finished = last(this.state.solutionSteps)?.finished
    return {
      fetched: fetched < 0 ? 0 : fetched,
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
    maxHeight,
    timeLimit
  ) => {
    this.backendClient.startAlgorithm(
      strategy,
      boxLength,
      numRectangles,
      minWidth,
      maxWidth,
      minHeight,
      maxHeight,
      timeLimit
    )(this.loadStartSolutionStepIntoState.bind(this))
  }

  startFromInstance = (strategy, instanceId, timeLimit) => {
    this.backendClient.startAlgorithmFromInstance(strategy, instanceId, timeLimit)(
      this.loadStartSolutionStepIntoState.bind(this)
    )
  }

  loadStartSolutionStepIntoState(startSolutionStep) {
    console.log(startSolutionStep)
    this.setState(oldState => ({
      ...oldState,
      running: true,
      runId: startSolutionStep.data.runId,
      solutionSteps: [startSolutionStep.data],
      currentStepIndex: 0
    }))
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
      lastLoadedStep + (this.state.combineSteps ? this.fetchCombinedSolutionStepsCount : this.fetchSolutionStepsCount),
      this.state.combineSteps
    )(solutionSteps => {
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

  toggleCombineSteps(active) {
    this.setState(oldState => ({
      ...oldState,
      combineSteps: active
    }))
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
    this.setState(oldState => ({
      ...oldState,
      currentStepIndex: actualIndex,
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

  toggleShowRectangleIds(active) {
    this.setState(oldState => ({
      ...oldState,
      showRectangleIds: active
    }))
  }

  getAutomaticVisualization = () => this.state.automaticVisualization
  getCurrentStepIndex = () => this.state.currentStepIndex
  getShowRectangleIds = () => this.state.showRectangleIds

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
          start={this.start}
          startFromInstance={this.startFromInstance}
          toggleCombineSteps={this.toggleCombineSteps.bind(this)}
          toggleShowRectangleIds={this.toggleShowRectangleIds.bind(this)}
          getShowRectangleIds={this.getShowRectangleIds.bind(this)}
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