import React, {Component} from 'react'
import Header from "./Header";
import Content from "./Content";
import BackendClient from "../client/BackendClient";

class App extends Component {

  backendClient = new BackendClient()

  state = {
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
        placement: startSolution.data.placement
      })
      console.log(this.state)
    })
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