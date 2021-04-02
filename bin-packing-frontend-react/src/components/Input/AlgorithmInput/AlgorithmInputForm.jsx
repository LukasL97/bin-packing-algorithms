import React, {Component} from 'react'
import NumericalInputFormRow from '../NumericalInputFormRow'
import StrategySelectorFormRow from './StrategySelectorFormRow'
import AlgorithmStrategies from '../../../utils/AlgorithmStrategies'
import ToggleFormRow from '../ToggleFormRow'
import InstanceLoaderFormRow from './InstanceLoaderFormRow'

class AlgorithmInputForm extends Component {

  constructor(props) {
    super(props)
    this.state = {
      strategy: AlgorithmStrategies.getDefaultStrategyId(),
      boxLength: '',
      numRectangles: '',
      minWidth: '',
      maxWidth: '',
      minHeight: '',
      maxHeight: '',
      timeLimit: '',
      useExistingInstance: false,
      instance: ''
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
    const timeLimit = this.state.timeLimit === '' ? null : this.state.timeLimit * 1000
    if (this.state.useExistingInstance) {
      this.props.startFromInstance(
        this.state.strategy,
        this.state.instance,
        timeLimit
      )
    } else {
      this.props.start(
        this.state.strategy,
        this.state.boxLength,
        this.state.numRectangles,
        this.state.minWidth,
        this.state.maxWidth,
        this.state.minHeight,
        this.state.maxHeight,
        timeLimit
      )
    }
  }

  handleLoadExistingInstanceChange(event) {
    this.setState(oldState => (
      {
        ...oldState,
        useExistingInstance: event.target.checked
      }
    ))
  }

  handleInstanceChange(id, boxLength, numRectangles, minWidth, maxWidth, minHeight, maxHeight) {
    this.setState(oldState => (
      {
        ...oldState,
        boxLength: boxLength,
        numRectangles: numRectangles,
        minWidth: minWidth,
        maxWidth: maxWidth,
        minHeight: minHeight,
        maxHeight: maxHeight,
        instance: id
      }
    ))
  }

  setDefaultInstanceId(id) {
    this.setState(oldState => (
      {
        ...oldState,
        instance: id
      }
    ))
  }

  render() {

    return (
      <div className="input-container-element input-form">
        <h3>Algorithm Input</h3>
        <form>
          <StrategySelectorFormRow onChange={this.handleTextualInputChange('strategy').bind(this)}/>
          <ToggleFormRow
            label={'Load existing instance'}
            nam={'load-existing-instance'}
            value={false}
            onToggle={this.handleLoadExistingInstanceChange.bind(this)}
          />
          {
            this.state.useExistingInstance ?
              <InstanceLoaderFormRow
                handleInstanceChange={this.handleInstanceChange.bind(this)}
                setDefaultInstanceId={this.setDefaultInstanceId.bind(this)}
              /> :
              null
          }
          <NumericalInputFormRow
            label={'Box Length'}
            name={'box-length'}
            value={this.state.boxLength}
            onChange={this.handleNumericalInputChange('boxLength').bind(this)}
          />
          <NumericalInputFormRow
            label={'Number of Rectangles'}
            name={'num-rectangles'}
            value={this.state.numRectangles}
            onChange={this.handleNumericalInputChange('numRectangles').bind(this)}
          />
          <NumericalInputFormRow
            label={'Min. Width'}
            name={'min-width'}
            value={this.state.minWidth}
            onChange={this.handleNumericalInputChange('minWidth').bind(this)}
          />
          <NumericalInputFormRow
            label={'Max. Width'}
            name={'max-width'}
            value={this.state.maxWidth}
            onChange={this.handleNumericalInputChange('maxWidth').bind(this)}
          />
          <NumericalInputFormRow
            label={'Min. Height'}
            name={'min-height'}
            value={this.state.minHeight}
            onChange={this.handleNumericalInputChange('minHeight').bind(this)}
          />
          <NumericalInputFormRow
            label={'Max. Height'}
            name={'max-height'}
            value={this.state.maxHeight}
            onChange={this.handleNumericalInputChange('maxHeight').bind(this)}
          />
          <NumericalInputFormRow
            label={'Time Limit (in s)'}
            name={'time-limit'}
            value={this.state.timeLimit}
            onChange={this.handleNumericalInputChange('timeLimit').bind(this)}
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