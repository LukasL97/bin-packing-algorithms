import React, {Component} from 'react'
import AlgorithmInputForm from './Input/AlgorithmInput/AlgorithmInputForm'
import AlgorithmDisplay from './AlgorithmDisplay'
import VisualizationConfigForm from './Input/VisualizationConfig/VisualizationConfigForm'
import AlgorithmProgressChart from './AlgorithmProgressChart'


class Content extends Component {

  render() {
    const {
      getCurrentSolutionStep,
      getRectanglesLastUpdate,
      start,
      visualizationIterationPeriodDefault,
      updateVisualizationIterationPeriod,
      toggleAutomaticVisualization,
      getAutomaticVisualization,
      getCurrentStepIndex,
      moveCurrentStepIndex,
      getProgress
    } = this.props

    return (
      <div className="content-container">
        <div className="input-container">
          <AlgorithmInputForm start={start}/>
          <VisualizationConfigForm
            visualizationIterationPeriodDefault={visualizationIterationPeriodDefault}
            updateVisualizationIterationPeriod={updateVisualizationIterationPeriod}
            toggleAutomaticVisualization={toggleAutomaticVisualization}
            getAutomaticVisualization={getAutomaticVisualization}
            getCurrentStepIndex={getCurrentStepIndex}
            moveCurrentStepIndex={moveCurrentStepIndex}
          />
          <AlgorithmProgressChart getProgress={getProgress}/>
        </div>
        <AlgorithmDisplay
          getCurrentSolutionStep={getCurrentSolutionStep}
          getRectanglesLastUpdate={getRectanglesLastUpdate}
        />
      </div>
    )
  }
}

export default Content
