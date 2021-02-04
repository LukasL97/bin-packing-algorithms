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
      ).then(startSolution => callback(startSolution))
    }
  }

}

export default BackendClient