import axios from "axios";

class BackendClient {

  constructor() {
    var backendHost = process.env.REACT_APP_API_HOST
    if (typeof backendHost == 'undefined') {
      backendHost = 'localhost'
    }
    var backendPort = process.env.REACT_APP_API_PORT
    if (typeof backendPort == 'undefined') {
      backendPort = 9000
    }
    axios.defaults.baseURL = 'http://' + backendHost + ':' + backendPort
    console.info('Setup API connection to ' + axios.defaults.baseURL)
  }

  startRectanglesPlacement(strategy, boxLength, numRectangles, minWidth, maxWidth, minHeight, maxHeight) {
    return (callback) => {
      console.trace('Starting algorithm')
      axios.put(
        '/rectanglesPlacement/start',
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
          }
        }
      ).then(startSolutionStep => callback(startSolutionStep))
    }
  }

  fetchSolutionSteps(runId, minStep, maxStep) {
    return (callback) => {
      console.trace('Fetching steps ' + minStep  + ' - ' + maxStep + ' for runId ' + runId)
      axios.get(
        '/rectanglesPlacement/steps',
        {
          params: {
            runId: runId,
            minStep: minStep,
            maxStep: maxStep
          }
        }
      ).then(solutionSteps => callback(solutionSteps))
    }
  }

}

export default BackendClient