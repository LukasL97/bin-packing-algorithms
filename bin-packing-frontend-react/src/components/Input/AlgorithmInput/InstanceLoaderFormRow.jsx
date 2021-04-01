import React, {Component} from 'react'
import BackendClient from '../../../client/BackendClient'

class InstanceLoaderFormRow extends Component {

  constructor(props) {
    super(props)
    this.state = {
      instances: []
    }
    this.backendClient = new BackendClient()
  }

  loadInstancesIntoState(instances) {
    this.setState(oldState => (
      {
        ...oldState,
        instances: instances
      }
    ))
  }

  refreshInstances() {
    this.backendClient.fetchAllInstances()(instances => {
      this.loadInstancesIntoState(instances.data)
      this.props.setDefaultInstanceId(this.state.instances.length > 0 ? this.state.instances[0].id : '')
    })
  }

  handleInstanceChange(event) {
    const id = event.target.value
    const instance = this.state.instances.find(i => i.id === id)
    this.props.handleInstanceChange(
      id,
      instance.boxLength,
      instance.numRectangles,
      instance.minWidth,
      instance.maxWidth,
      instance.minHeight,
      instance.maxHeight
    )
  }

  componentDidMount() {
    this.refreshInstances()
  }

  render() {
    const instances = this.state.instances.map(instance =>
      <option key={instance.id} value={instance.id}>
        {instance.creationDate + ' (' + instance.boxLength + ', ' + instance.numRectangles + ', ' + instance.minWidth + ', ' + instance.maxWidth + ', ' + instance.minHeight + ', ' + instance.maxHeight + ')'}
      </option>
    )

    return (
      <div className='instance-loader drop-down-form-row'>
        <select
          id='instance'
          name='instance'
          onChange={this.handleInstanceChange.bind(this)}
          onLoad={this.handleInstanceChange.bind(this)}
        >
          {instances}
        </select>
      </div>
    )
  }

}

export default InstanceLoaderFormRow