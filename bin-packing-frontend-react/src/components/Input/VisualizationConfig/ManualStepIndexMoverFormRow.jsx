import React, {useState} from 'react'

const ManualStepIndexMoverFormRow = (props) => {

  const {getCurrentStepIndex, moveCurrentStepIndex} = props

  const [displayedStepIndex, setDisplayedStepIndex] = useState(getCurrentStepIndex())

  function moveCurrentStepIndexWithoutPageRefresh(index) {
    return event => {
      event.preventDefault()
      moveCurrentStepIndex(parseInt(index))
      setDisplayedStepIndex(parseInt(index))
    }
  }

  return (
    <div className="form-row manual-step-index-mover-form-row">
      <div className="manual-step-index-mover-form-row-element">
        <button onClick={moveCurrentStepIndexWithoutPageRefresh(getCurrentStepIndex() - 1)}>Previous</button>
      </div>
      <div className="manual-step-index-mover-form-row-element">
        <input
          id="current-step-index" name="current-step-index" type="number" value={displayedStepIndex}
          onBlur={() => moveCurrentStepIndex(displayedStepIndex)}
          onKeyPress={event => {
            if (event.key === 'Enter') {
              event.preventDefault()
              event.target.blur()
            }
          }}
          onChange={event => setDisplayedStepIndex(parseInt(event.target.value))}
        />
      </div>
      <div className="manual-step-index-mover-form-row-element">
        <button onClick={moveCurrentStepIndexWithoutPageRefresh(getCurrentStepIndex() + 1)}>Next</button>
      </div>
    </div>
  )

}

export default ManualStepIndexMoverFormRow