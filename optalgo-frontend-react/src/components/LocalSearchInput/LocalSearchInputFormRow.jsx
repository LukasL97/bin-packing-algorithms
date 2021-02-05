const LocalSearchInputFormRow = (props) => {

  const {label, name, value, onChange} = props

  return (
    <div className="local-search-input-form-row">
      <label htmlFor={name}>{label}</label>
      <input id={name} name={name} type="number" value={value} onChange={onChange}/>
    </div>
  )
}

export default LocalSearchInputFormRow