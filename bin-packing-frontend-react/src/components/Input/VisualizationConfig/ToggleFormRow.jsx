import React, {useState} from 'react'

const ToggleFormRow = (props) => {

  const {label, name, value, onToggle} = props

  const [checked, setChecked] = useState(value)

  return (
    <div className="labeled-form-row toggle-form-row">
      <label className="form-row-label" htmlFor={name}>{label}</label>
      <label className="switch">
        <input
          id={name}
          name={name}
          type="checkbox"
          onClick={() => setChecked(!checked)}
          checked={checked}
          onChange={onToggle}
        />
        <span className="slider round"/>
      </label>
    </div>
  )
}

export default ToggleFormRow