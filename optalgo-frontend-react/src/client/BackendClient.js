import axios from "axios";

class BackendClient {

  constructor() {
    axios.defaults.baseURL = 'http://localhost:9000'
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