import React, {Component} from 'react'
import NumericalInputFormRow from "./NumericalInputFormRow";

class AlgorithmInputForm extends Component {

  constructor(props) {
    super(props);
    this.state = {
      boxLength: "",
      numRectangles: "",
      minWidth: "",
      maxWidth: "",
      minHeight: "",
      maxHeight: ""
    }
  }

  handleInputChange(inputId) {
    return (event) => {
      this.setState(oldState => (
        {
          ...oldState,
          [inputId]: parseInt(event.target.value)
        }
      ))
    }
  }

  startWithoutPageRefresh(event) {
    event.preventDefault()
    this.props.start(
      this.state.boxLength,
      this.state.numRectangles,
      this.state.minWidth,
      this.state.maxWidth,
      this.state.minHeight,
      this.state.maxHeight
    )
  }

  render() {

    return (
      <div className="algorithm-input-form">
        <form>
          <NumericalInputFormRow
            label={"Box Length"}
            name={"box-length"}
            value={this.state.boxLength}
            onChange={this.handleInputChange('boxLength').bind(this)}
          />
          <NumericalInputFormRow
            label={"Number of Rectangles"}
            name={"num-rectangles"}
            value={this.state.numRectangles}
            onChange={this.handleInputChange('numRectangles').bind(this)}
          />
          <NumericalInputFormRow
            label={"Min. Width"}
            name={"min-width"}
            value={this.state.minWidth}
            onChange={this.handleInputChange('minWidth').bind(this)}
          />
          <NumericalInputFormRow
            label={"Max. Width"}
            name={"max-width"}
            value={this.state.maxWidth}
            onChange={this.handleInputChange('maxWidth').bind(this)}
          />
          <NumericalInputFormRow
            label={"Min. Height"}
            name={"min-height"}
            value={this.state.minHeight}
            onChange={this.handleInputChange('minHeight').bind(this)}
          />
          <NumericalInputFormRow
            label={"Max. Height"}
            name={"max-height"}
            value={this.state.maxHeight}
            onChange={this.handleInputChange('maxHeight').bind(this)}
          />
          <button onClick={this.startWithoutPageRefresh.bind(this)}>Start</button>
        </form>
      </div>
    )
  }

}

export default AlgorithmInputForm