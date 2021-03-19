import React, {Component} from 'react'
import NumericalInputFormRow from "../NumericalInputFormRow";
import StrategySelectorFormRow from "./StrategySelectorFormRow";
import AlgorithmStrategies from "../../../utils/AlgorithmStrategies";

class AlgorithmInputForm extends Component {

  constructor(props) {
    super(props);
    this.state = {
      strategy: AlgorithmStrategies.getDefaultStrategyId(),
      boxLength: "",
      numRectangles: "",
      minWidth: "",
      maxWidth: "",
      minHeight: "",
      maxHeight: ""
    }
  }

  handleInputChange(valueParser) {
    return (inputId) => {
      return (event) => {
        this.setState(oldState => (
          {
            ...oldState,
            [inputId]: valueParser(event.target.value)
          }
        ))
      }
    }
  }

  handleNumericalInputChange = this.handleInputChange(parseInt)
  handleTextualInputChange = this.handleInputChange(v => v)

  startWithoutPageRefresh(event) {
    event.preventDefault()
    this.props.start(
      this.state.strategy,
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
      <div className="input-container-element input-form">
        <h3>Algorithm Input</h3>
        <form>
          <StrategySelectorFormRow onChange={this.handleTextualInputChange('strategy').bind(this)}/>
          <NumericalInputFormRow
            label={"Box Length"}
            name={"box-length"}
            value={this.state.boxLength}
            onChange={this.handleNumericalInputChange('boxLength').bind(this)}
          />
          <NumericalInputFormRow
            label={"Number of Rectangles"}
            name={"num-rectangles"}
            value={this.state.numRectangles}
            onChange={this.handleNumericalInputChange('numRectangles').bind(this)}
          />
          <NumericalInputFormRow
            label={"Min. Width"}
            name={"min-width"}
            value={this.state.minWidth}
            onChange={this.handleNumericalInputChange('minWidth').bind(this)}
          />
          <NumericalInputFormRow
            label={"Max. Width"}
            name={"max-width"}
            value={this.state.maxWidth}
            onChange={this.handleNumericalInputChange('maxWidth').bind(this)}
          />
          <NumericalInputFormRow
            label={"Min. Height"}
            name={"min-height"}
            value={this.state.minHeight}
            onChange={this.handleNumericalInputChange('minHeight').bind(this)}
          />
          <NumericalInputFormRow
            label={"Max. Height"}
            name={"max-height"}
            value={this.state.maxHeight}
            onChange={this.handleNumericalInputChange('maxHeight').bind(this)}
          />
          <div className="input-form-button-container">
            <button onClick={this.startWithoutPageRefresh.bind(this)}>Start</button>
          </div>
        </form>
      </div>
    )
  }

}

export default AlgorithmInputForm