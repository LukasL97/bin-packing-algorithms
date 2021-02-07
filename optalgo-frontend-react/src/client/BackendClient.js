import axios from "axios";

class BackendClient {

  constructor() {
    axios.defaults.baseURL = 'http://localhost:9000'
  }

  startRectanglesPlacement(strategy, boxLength, numRectangles, minWidth, maxWidth, minHeight, maxHeight) {
    return (callback) => {
      console.trace("Starting algorithm")
      axios.put('/rectanglesPlacement/start',
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

  getRndInt(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
  }

  createRandomSolution() {
    return {
      placement: [
        {
          box: {id: 1, width: 100, height: 100},
          coordinates: {x: this.getRndInt(0, 20), y: this.getRndInt(0, 20)},
          rectangle: {id: 1, width: this.getRndInt(40, 80), height: this.getRndInt(40, 80)}
        }
      ]
    }
  }

  // TODO: integrate API when ready
  fetchSolutionSteps(runId, minStep, maxStep) {
    return [...Array(maxStep - minStep + 1).keys()].map(i => {
        return {
          runId: runId,
          step: minStep + i,
          solution: this.createRandomSolution()
        }
      }
    )
  }

}

export default BackendClient