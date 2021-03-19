import Chart from 'react-apexcharts'
import React from 'react'

const AlgorithmProgressChart = (props) => {

  const progress = props.getProgress()

  const series = [{
    data: [progress.fetched, progress.visualized]
  }]

  const options = {
    chart: {
      type: 'bar'
    },
    colors: ['#5995DA'],
    plotOptions: {
      bar: {
        borderRadius: 4,
        horizontal: true
      }
    },
    dataLabels: {
      style: {
        colors: ['black']
      }
    },
    xaxis: {
      categories: [
        'Fetched',
        'Visualized'
      ]
    }
  }

  return (
    <div className="input-container-element progress-chart-container">
      <h3>Algorithm Progress</h3>
      <Chart options={options} series={series} type="bar"/>
      <div className="progress-chart-finished-indicators">
        {progress.finished ? <p>Computation complete</p> : null}
        {(progress.finished && progress.visualized === progress.fetched) ? <p>Visualization complete</p> : null}
      </div>
    </div>
  )

}

export default AlgorithmProgressChart