class AlgorithmStrategies {

  static getAll() {
    return [
      // {
      //   id: 'greedy randomSelection',
      //   name: 'Greedy - Random Selection'
      // },
      // {
      //   id: 'greedy sizeOrdered',
      //   name: 'Greedy - Size-ordered'
      // },
      // {
      //   id: 'greedy2 randomSelection',
      //   name: 'Quick Greedy - Random Selection'
      // },
      {
        id: 'greedy2 sizeOrdered',
        name: 'Greedy - Size-ordered'
      },
      {
        id: 'greedy boxClosing',
        name: 'Greedy - Box Closing'
      },
      // {
      //   id: 'localSearch geometryBased',
      //   name: 'Local Search - Geometry-based'
      // },
      // {
      //   id: 'localSearch eventuallyFeasibleGeometryBased',
      //   name: 'Local Search - Geometry-based (eventually feasible)'
      // },
      {
        id: 'localSearch boxMerging',
        name: 'Local Search - Geometric'
      },
      {
        id: 'localSearch overlapping',
        name: 'Local Search - Overlapping'
      },
      {
        id: 'localSearch rectanglePermutation',
        name: 'Local Search - Rectangle Permutation'
      }
    ]
  }

  static getDefaultStrategyId() {
    return this.getAll()[0].id
  }

}

export default AlgorithmStrategies
