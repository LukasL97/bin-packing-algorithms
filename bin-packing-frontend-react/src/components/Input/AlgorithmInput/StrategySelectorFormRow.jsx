import AlgorithmStrategies from '../../../utils/AlgorithmStrategies'

const StrategySelectorFormRow = (props) => {

  const strategies = AlgorithmStrategies.getAll().map(strat =>
    <option key={strat.id} value={strat.id}>{strat.name}</option>
  )

  return (
    <div className='strategy-selector drop-down-form-row'>
      <select id='strategy' name='strategy' onChange={props.onChange} onLoad={props.onChange}>
        {strategies}
      </select>
    </div>
  )
}

export default StrategySelectorFormRow