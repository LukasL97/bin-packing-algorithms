class AlgorithmStrategies {

  static getAll() {
    return [
      {
        id: "greedy randomSelection",
        name: "Greedy - Random Selection"
      },
      {
        id: "localSearch geometryBased",
        name: "Local Search - Geometry-based"
      }
    ]
  }

  static getDefaultStrategyId() {
    return this.getAll()[0].id
  }

}

export default AlgorithmStrategies
