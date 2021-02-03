const LocalSearchInputFormRow = (props) => {

  const {label, name} = props

  return (
    <div className="local-search-input-form-row">
      <label htmlFor={name}>{label}</label>
      <input id={name} name={name} type="text"/>
    </div>
  )
}

export default LocalSearchInputFormRow