import React, {Component} from 'react'
import NumericalInputFormRow from '../NumericalInputFormRow'
import ToggleFormRow from './ToggleFormRow'

class VisualizationConfigForm extends Component {

  constructor(props) {
    super(props)
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

  handleAutoModeChange(event) {
    this.props.toggleAutomaticVisualization(event.target.checked)
  }

  render() {
    return (
      <div className="input-container-element visualization-config-form">
        <h3>Visualization Config</h3>
        <form>
          <ToggleFormRow
            label={'Auto Mode'}
            name={'auto-mode'}
            value={true}
            onToggle={this.handleAutoModeChange.bind(this)}
          />
          {
            this.props.getAutomaticVisualization() ?
              <NumericalInputFormRow
                label={'ms / Iteration'}
                name={'ms-per-iteration'}
                value={this.state.visualizationIterationPeriod}
                onChange={this.handleMsPerIterationChange.bind(this)}
              /> :
              null
          }
        </form>
      </div>
    )
  }

}

export default VisualizationConfigForm
