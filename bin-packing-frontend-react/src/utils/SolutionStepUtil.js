class SolutionStepUtil {

  static zipPlacementsByRectangleId(placementA, placementB) {

    function rectangleIdComparator(placingA, placingB) {
      return placingA.rectangle.id - placingB.rectangle.id
    }

    function zipSortedPlacementsByRectangleId(placementA, placementB) {
      if (placementA.length === 0) {
        return placementB.map(p => {
          return {
            left: null,
            right: p
          }
        })
      }
      if (placementB.length === 0) {
        return placementA.map(p => {
          return {
            left: p,
            right: null
          }
        })
      }
      const placingA = placementA[0]
      const placingB = placementB[0]
      const comp = rectangleIdComparator(placingA, placingB)
      if (comp === 0) {
        return [
          {
            left: placingA,
            right: placingB
          },
          ...zipSortedPlacementsByRectangleId(placementA.slice(1), placementB.slice(1))
        ]
      } else if (comp < 0) {
        return [
          {
            left: placingA,
            right: null
          },
          ...zipSortedPlacementsByRectangleId(placementA.slice(1), placementB)
        ]
      } else {
        return [
          {
            left: null,
            right: placingB
          },
          ...zipSortedPlacementsByRectangleId(placementA, placementB.slice(1))
        ]
      }
    }

    const placementASorted = [...placementA].sort(rectangleIdComparator)
    const placementBSorted = [...placementB].sort(rectangleIdComparator)

    return zipSortedPlacementsByRectangleId(placementASorted, placementBSorted)
  }

}

export default SolutionStepUtil