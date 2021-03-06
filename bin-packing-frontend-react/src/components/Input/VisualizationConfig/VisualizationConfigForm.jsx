import React, {Component} from 'react'
import NumericalInputFormRow from '../NumericalInputFormRow'

class VisualizationConfigForm extends Component {

  constructor(props) {
    super(props);
    this.state = {
      visualizationIterationPeriod: this.props.visualizationIterationPeriodDefault
    }
  }

  handleMsPerIterationChange(event) {
    const newPeriod = event.target.value
    this.setState({
      visualizationIterationPeriod: newPeriod
    })
    this.props.updateVisualizationIterationPeriod(newPeriod)
  }

  render() {
    return (
      <div className="visualization-config-form">
        <h3>Visualization Config</h3>
        <form>
          <NumericalInputFormRow
            label={'ms / Iteration'}
            name={'ms-per-iteration'}
            value={this.state.visualizationIterationPeriod}
            onChange={this.handleMsPerIterationChange.bind(this)}
          />
        </form>
      </div>
    )
  }

}

export default VisualizationConfigForm
