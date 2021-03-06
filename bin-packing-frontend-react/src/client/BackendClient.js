import axios from 'axios'

class BackendClient {

  constructor() {
    let apiUrl = process.env.REACT_APP_API_URL
    if (typeof apiUrl == 'undefined') {
      apiUrl = 'http://localhost:9000'
    }
    axios.defaults.baseURL = apiUrl
    console.info('Setup API connection to ' + axios.defaults.baseURL)
  }

  startAlgorithm(strategy, boxLength, numRectangles, minWidth, maxWidth, minHeight, maxHeight, timeLimit) {
    return (callback) => {
      console.trace('Starting algorithm')
      axios.put(
        '/binPacking/start',
        {
          strategy: strategy,
          boxLength: boxLength,
          numRectangles: numRectangles,
          rectanglesWidthRange: {
            min: minWidth,
            max: maxWidth
          },
          rectanglesHeightRange: {
            min: minHeight,
            max: maxHeight
          },
          timeLimit: timeLimit
        }
      ).then(startSolutionStep => callback(startSolutionStep))
    }
  }

  startAlgorithmFromInstance(strategy, instanceId, timeLimit) {
    return (callback) => {
      console.trace('Starting algorithm from instance with id ' + instanceId)
      axios.put(
        '/binPacking/startFromInstance',
        {
          strategy: strategy,
          instanceId: instanceId,
          timeLimit: timeLimit
        }
      ).then(startSolutionStep => callback(startSolutionStep))
    }
  }

  fetchSolutionSteps(runId, minStep, maxStep, combined) {
    return (callback) => {
      console.trace('Fetching steps ' + minStep + ' - ' + maxStep + ' for runId ' + runId)
      axios.get(
        '/binPacking/steps',
        {
          params: {
            runId: runId,
            minStep: minStep,
            maxStep: maxStep,
            combined: combined
          }
        }
      ).then(solutionSteps => callback(solutionSteps))
    }
  }

  fetchAllInstances() {
    return (callback) => {
      console.trace('Fetch all instances')
      axios.get(
        '/instances'
      ).then(instances => callback(instances))
    }
  }

}

export default BackendClient