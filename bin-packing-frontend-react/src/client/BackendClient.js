import axios from "axios";

class BackendClient {

  constructor() {
    let apiUrl = process.env.REACT_APP_API_URL
    if (typeof apiUrl == 'undefined') {
      apiUrl = 'http://localhost:9000'
    }
    axios.defaults.baseURL = apiUrl
    console.info('Setup API connection to ' + axios.defaults.baseURL)
  }

  startAlgorithm(strategy, boxLength, numRectangles, minWidth, maxWidth, minHeight, maxHeight) {
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
          }
        }
      ).then(startSolutionStep => callback(startSolutionStep))
    }
  }

  fetchSolutionSteps(runId, minStep, maxStep) {
    return (callback) => {
      console.trace('Fetching steps ' + minStep  + ' - ' + maxStep + ' for runId ' + runId)
      axios.get(
        '/binPacking/steps',
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