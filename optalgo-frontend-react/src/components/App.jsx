import React, {Component} from 'react'
import Header from "./Header";
import Content from "./Content";
import BackendClient from "../client/BackendClient";

class App extends Component {

  backendClient = new BackendClient()

  state = {
    placement: []
  }

  // TODO: get only for box id
  getRectangles = () => {
    console.log('STATE:')
    console.log(this.state)
    return this.state.placement.map(placing => {
      return {
        x: placing.coordinates.x,
        y: placing.coordinates.y,
        width: placing.rectangle.width,
        height: placing.rectangle.height
      }
    })
  }

  // TODO: read from form
  start = () => {
    this.backendClient.startRectanglesPlacement(
      'geometryBased',
      100,
      2,
      30,
      80,
      10,
      90
    )(startSolution => {
      console.log(startSolution)
      this.setState({
        placement: startSolution.data.placement
      })
    })
  }

  render() {
    return (
      <div className="main">
        <Header/>
        <Content getRectangles={this.getRectangles} start={this.start}/>
      </div>
    )
  }
}

export default App