import React, {Component} from 'react'
import LocalSearchInputFormRow from "./LocalSearchInputFormRow";

class LocalSearchInputForm extends Component {

  render() {
    return (
      <div className="local-search-input-form">
        <form>
          <LocalSearchInputFormRow label={"Box Length"} name={"box-length"}/>
          <LocalSearchInputFormRow label={"Number of Rectangles"} name={"num-rectangles"}/>
          <LocalSearchInputFormRow label={"Min. Width"} name={"min-width"}/>
          <LocalSearchInputFormRow label={"Max. Width"} name={"max-width"}/>
          <LocalSearchInputFormRow label={"Min. Height"} name={"min-height"}/>
          <LocalSearchInputFormRow label={"Max. Height"} name={"max-height"}/>
          <button>Start</button>
        </form>
      </div>
    )
  }

}

export default LocalSearchInputForm