import React, {Component} from 'react'
import NumericalInputFormRow from '../NumericalInputFormRow'
import ToggleFormRow from '../ToggleFormRow'
import ManualStepIndexMoverFormRow from './ManualStepIndexMoverFormRow'

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

  handleCombineStepsChange(event) {
    this.props.toggleCombineSteps(event.target.checked)
  }

  handleShowRectangleIdsChange(event) {
    this.props.toggleShowRectangleIds(event.target.checked)
  }

  render() {
    return (
      <div className="input-container-element visualization-config-form">
        <h3>Visualization Config</h3>
        <form>
          <ToggleFormRow
            label={'Combine Steps'}
            name={'combine-steps'}
            value={false}
            onToggle={this.handleCombineStepsChange.bind(this)}
          />
          <ToggleFormRow
            label={'Show Rectangle IDs'}
            name={'show-rectangle-ids'}
            value={false}
            onToggle={this.handleShowRectangleIdsChange.bind(this)}
          />
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
              <ManualStepIndexMoverFormRow
                getCurrentStepIndex={this.props.getCurrentStepIndex}
                moveCurrentStepIndex={this.props.moveCurrentStepIndex}
              />
          }
        </form>
      </div>
    )
  }

}

export default VisualizationConfigForm
