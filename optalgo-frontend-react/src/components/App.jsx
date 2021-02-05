import React, {Component} from 'react'
import Header from "./Header";
import Content from "./Content";
import BackendClient from "../client/BackendClient";

class App extends Component {

  fetchSolutionPeriod = 100

  backendClient = new BackendClient()

  state = {
    running: false,
    placement: []
  }

  getRectanglesPlacement = () => this.state.placement

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
    )(startSolution => {
      console.log(startSolution)
      this.setState({
        running: true,
        placement: startSolution.data.placement
      })
    })
  }

  fetchLatestSolution = () => {
    this.setState({
      running: this.state.running, // TODO: recognize finished run
      placement: this.backendClient.fetchCurrentSolution().placement
    })
  }

  componentDidMount() {
    this.fetchSolutionInterval = setInterval(
      () => {
        if (this.state.running) {
          this.fetchLatestSolution()
          console.log(this.state)
        }
      },
      this.fetchSolutionPeriod
    )
  }

  componentWillUnmount() {
    clearInterval(this.fetchSolutionInterval)
  }

  render() {
    return (
      <div className="main">
        <Header/>
        <Content getRectanglesPlacement={this.getRectanglesPlacement} start={this.start}/>
      </div>
    )
  }
}

export default App