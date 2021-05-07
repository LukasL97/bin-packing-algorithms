(this["webpackJsonpbin-packing-frontend"]=this["webpackJsonpbin-packing-frontend"]||[]).push([[0],{117:function(t,e,n){"use strict";n.r(e);var a=n(6),i=n(47),s=n.n(i),o=(n(53),n(18)),r=n(5),c=n(9),l=n(10),u=n(21),h=n(12),d=n(11),g=n(0),m=function(){return Object(g.jsx)("div",{className:"header-container",children:Object(g.jsx)("header",{children:Object(g.jsx)("h1",{children:"Bin Packing Algorithms"})})})},b=n(26),p=function(t){var e=t.label,n=t.name,a=t.value,i=t.onChange;return Object(g.jsxs)("div",{className:"form-row labeled-form-row numerical-input-form-row",children:[Object(g.jsx)("label",{className:"form-row-label",htmlFor:n,children:e}),Object(g.jsx)("input",{id:n,name:n,type:"number",value:a,onChange:i})]})},j=function(){function t(){Object(c.a)(this,t)}return Object(l.a)(t,null,[{key:"getAll",value:function(){return[{id:"greedy2 sizeOrdered",name:"Greedy - Size-ordered"},{id:"greedy boxClosing",name:"Greedy - Box Closing"},{id:"localSearch boxMerging",name:"Local Search - Geometric"},{id:"localSearch overlapping",name:"Local Search - Overlapping"},{id:"localSearch rectanglePermutation",name:"Local Search - Rectangle Permutation"}]}},{key:"getDefaultStrategyId",value:function(){return this.getAll()[0].id}}]),t}(),f=function(t){var e=j.getAll().map((function(t){return Object(g.jsx)("option",{value:t.id,children:t.name},t.id)}));return Object(g.jsx)("div",{className:"strategy-selector drop-down-form-row",children:Object(g.jsx)("select",{id:"strategy",name:"strategy",onChange:t.onChange,onLoad:t.onChange,children:e})})},x=n(27),v=function(t){var e=t.label,n=t.name,i=t.value,s=t.onToggle,o=Object(a.useState)(i),r=Object(x.a)(o,2),c=r[0],l=r[1];return Object(g.jsxs)("div",{className:"form-row labeled-form-row toggle-form-row",children:[Object(g.jsx)("label",{className:"form-row-label",htmlFor:n,children:e}),Object(g.jsxs)("label",{className:"switch",children:[Object(g.jsx)("input",{id:n,name:n,type:"checkbox",onClick:function(){return l(!c)},checked:c,onChange:s}),Object(g.jsx)("span",{className:"slider round"})]})]})},S=n(16),I=n.n(S),O=function(){function t(){Object(c.a)(this,t);var e="https://bin-packing-backend.herokuapp.com";"undefined"==typeof e&&(e="http://localhost:9000"),I.a.defaults.baseURL=e,console.info("Setup API connection to "+I.a.defaults.baseURL)}return Object(l.a)(t,[{key:"startAlgorithm",value:function(t,e,n,a,i,s,o,r){return function(c){console.trace("Starting algorithm"),I.a.put("/binPacking/start",{strategy:t,boxLength:e,numRectangles:n,rectanglesWidthRange:{min:a,max:i},rectanglesHeightRange:{min:s,max:o},timeLimit:r}).then((function(t){return c(t)}))}}},{key:"startAlgorithmFromInstance",value:function(t,e,n){return function(a){console.trace("Starting algorithm from instance with id "+e),I.a.put("/binPacking/startFromInstance",{strategy:t,instanceId:e,timeLimit:n}).then((function(t){return a(t)}))}}},{key:"fetchSolutionSteps",value:function(t,e,n,a){return function(i){console.trace("Fetching steps "+e+" - "+n+" for runId "+t),I.a.get("/binPacking/steps",{params:{runId:t,minStep:e,maxStep:n,combined:a}}).then((function(t){return i(t)}))}}},{key:"fetchAllInstances",value:function(){return function(t){console.trace("Fetch all instances"),I.a.get("/instances").then((function(e){return t(e)}))}}}]),t}(),C=function(t){Object(h.a)(n,t);var e=Object(d.a)(n);function n(t){var a;return Object(c.a)(this,n),(a=e.call(this,t)).state={instances:[]},a.backendClient=new O,a}return Object(l.a)(n,[{key:"loadInstancesIntoState",value:function(t){this.setState((function(e){return Object(r.a)(Object(r.a)({},e),{},{instances:t})}))}},{key:"refreshInstances",value:function(){var t=this;this.backendClient.fetchAllInstances()((function(e){t.loadInstancesIntoState(e.data),t.props.setDefaultInstanceId(t.state.instances.length>0?t.state.instances[0].id:"")}))}},{key:"handleInstanceChange",value:function(t){var e=t.target.value,n=this.state.instances.find((function(t){return t.id===e}));this.props.handleInstanceChange(e,n.boxLength,n.numRectangles,n.minWidth,n.maxWidth,n.minHeight,n.maxHeight)}},{key:"componentDidMount",value:function(){this.refreshInstances()}},{key:"render",value:function(){var t=this.state.instances.map((function(t){return Object(g.jsx)("option",{value:t.id,children:t.creationDate+" ("+t.boxLength+", "+t.numRectangles+", "+t.minWidth+", "+t.maxWidth+", "+t.minHeight+", "+t.maxHeight+")"},t.id)}));return Object(g.jsx)("div",{className:"instance-loader drop-down-form-row",children:Object(g.jsx)("select",{id:"instance",name:"instance",onChange:this.handleInstanceChange.bind(this),onLoad:this.handleInstanceChange.bind(this),children:t})})}}]),n}(a.Component),y=function(t){Object(h.a)(n,t);var e=Object(d.a)(n);function n(t){var a;return Object(c.a)(this,n),(a=e.call(this,t)).handleNumericalInputChange=a.handleInputChange(parseInt),a.handleTextualInputChange=a.handleInputChange((function(t){return t})),a.state={strategy:j.getDefaultStrategyId(),boxLength:"",numRectangles:"",minWidth:"",maxWidth:"",minHeight:"",maxHeight:"",timeLimit:"",useExistingInstance:!1,instance:""},a}return Object(l.a)(n,[{key:"handleInputChange",value:function(t){var e=this;return function(n){return function(a){e.setState((function(e){return Object(r.a)(Object(r.a)({},e),{},Object(b.a)({},n,t(a.target.value)))}))}}}},{key:"startWithoutPageRefresh",value:function(t){t.preventDefault();var e=""===this.state.timeLimit?null:1e3*this.state.timeLimit;this.state.useExistingInstance?this.props.startFromInstance(this.state.strategy,this.state.instance,e):this.props.start(this.state.strategy,this.state.boxLength,this.state.numRectangles,this.state.minWidth,this.state.maxWidth,this.state.minHeight,this.state.maxHeight,e)}},{key:"handleLoadExistingInstanceChange",value:function(t){this.setState((function(e){return Object(r.a)(Object(r.a)({},e),{},{useExistingInstance:t.target.checked})}))}},{key:"handleInstanceChange",value:function(t,e,n,a,i,s,o){this.setState((function(c){return Object(r.a)(Object(r.a)({},c),{},{boxLength:e,numRectangles:n,minWidth:a,maxWidth:i,minHeight:s,maxHeight:o,instance:t})}))}},{key:"setDefaultInstanceId",value:function(t){this.setState((function(e){return Object(r.a)(Object(r.a)({},e),{},{instance:t})}))}},{key:"render",value:function(){return Object(g.jsxs)("div",{className:"input-container-element input-form",children:[Object(g.jsx)("h3",{children:"Algorithm Input"}),Object(g.jsxs)("form",{children:[Object(g.jsx)(f,{onChange:this.handleTextualInputChange("strategy").bind(this)}),Object(g.jsx)(v,{label:"Load existing instance",nam:"load-existing-instance",value:!1,onToggle:this.handleLoadExistingInstanceChange.bind(this)}),this.state.useExistingInstance?Object(g.jsx)(C,{handleInstanceChange:this.handleInstanceChange.bind(this),setDefaultInstanceId:this.setDefaultInstanceId.bind(this)}):null,Object(g.jsx)(p,{label:"Box Length",name:"box-length",value:this.state.boxLength,onChange:this.handleNumericalInputChange("boxLength").bind(this)}),Object(g.jsx)(p,{label:"Number of Rectangles",name:"num-rectangles",value:this.state.numRectangles,onChange:this.handleNumericalInputChange("numRectangles").bind(this)}),Object(g.jsx)(p,{label:"Min. Width",name:"min-width",value:this.state.minWidth,onChange:this.handleNumericalInputChange("minWidth").bind(this)}),Object(g.jsx)(p,{label:"Max. Width",name:"max-width",value:this.state.maxWidth,onChange:this.handleNumericalInputChange("maxWidth").bind(this)}),Object(g.jsx)(p,{label:"Min. Height",name:"min-height",value:this.state.minHeight,onChange:this.handleNumericalInputChange("minHeight").bind(this)}),Object(g.jsx)(p,{label:"Max. Height",name:"max-height",value:this.state.maxHeight,onChange:this.handleNumericalInputChange("maxHeight").bind(this)}),Object(g.jsx)(p,{label:"Time Limit (in s)",name:"time-limit",value:this.state.timeLimit,onChange:this.handleNumericalInputChange("timeLimit").bind(this)}),Object(g.jsx)("div",{className:"input-form-button-container",children:Object(g.jsx)("button",{onClick:this.startWithoutPageRefresh.bind(this),children:"Start"})})]})]})}}]),n}(a.Component),k=n(15),w=function(t){Object(h.a)(n,t);var e=Object(d.a)(n);function n(){var t;Object(c.a)(this,n);for(var a=arguments.length,i=new Array(a),s=0;s<a;s++)i[s]=arguments[s];return(t=e.call.apply(e,[this].concat(i))).boxFillColor="white",t.boxBorderColor="black",t.rectangleFillColorBase="grey",t.rectangleFillColorUpdated="red",t.rectangleBorderColor="black",t.rectangleOpacity=.7,t}return Object(l.a)(n,[{key:"getRectangleColor",value:function(t,e){return e.includes(t)?this.rectangleFillColorUpdated:this.rectangleFillColorBase}},{key:"render",value:function(){var t=this.props,e=t.id,n=t.unitLength,a=t.pixelLength,i=t.getRectangles,s=t.changedRectangleIds,o=t.getShowRectangleIds;function r(t){return t/n*a}var c=i().map(function(t){return Object(g.jsxs)(k.a,{x:r(t.x),y:r(t.y),width:r(t.width),height:r(t.height),children:[Object(g.jsx)(k.c,{width:r(t.width),height:r(t.height),fill:this.getRectangleColor(t.id,s),stroke:this.rectangleBorderColor,opacity:this.rectangleOpacity}),o()?Object(g.jsx)(k.e,{text:t.id,fontSize:10,padding:3}):null]})}.bind(this));return Object(g.jsx)("div",{className:"box",id:e,children:Object(g.jsx)(k.d,{width:a,height:a,children:Object(g.jsxs)(k.b,{children:[Object(g.jsx)(k.c,{x:0,y:0,width:a,height:a,fill:this.boxFillColor,stroke:this.boxBorderColor}),Object(g.jsx)(k.a,{x:0,y:0,children:c})]})})})}}]),n}(a.Component),R=function(t){Object(h.a)(n,t);var e=Object(d.a)(n);function n(){var t;Object(c.a)(this,n);for(var a=arguments.length,i=new Array(a),s=0;s<a;s++)i[s]=arguments[s];return(t=e.call.apply(e,[this].concat(i))).getCurrentSolutionStep=t.props.getCurrentSolutionStep,t.getShowRectangleIds=t.props.getShowRectangleIds,t.state={placement:[],step:0,update:{jsonClass:"UnchangedSolution"},permutation:null,showAllBoxes:!1},t.maxShownBoxes=50,t.boxPixelLength=300,t}return Object(l.a)(n,[{key:"getRectangles",value:function(t){var e=this;return function(){return e.state.placement.filter((function(e){return e.box.id===t})).map((function(t){return{x:t.coordinates.x,y:t.coordinates.y,width:t.rectangle.width,height:t.rectangle.height,id:t.rectangle.id}}))}}},{key:"getUnique",value:function(t){return Object(o.a)(new Set(t.map((function(t){return t.id})))).map((function(e){return t.find((function(t){return t.id===e}))}))}},{key:"getChangedRectangleIds",value:function(t){return"RectanglesChanged"===t.jsonClass?t.rectangleIds:[]}},{key:"handleShowAllBoxesChange",value:function(t){this.setState((function(e){return Object(r.a)(Object(r.a)({},e),{},{showAllBoxes:t.target.checked})}))}},{key:"render",value:function(){var t=this,e=this.getCurrentSolutionStep();e&&e.solution.placement!==this.state.placement&&(this.setState((function(t){return Object(r.a)(Object(r.a)({},t),{},{placement:e.solution.placement,step:e.step,update:e.solution.update,permutation:e.solution.permutation?e.solution.permutation:null})})),console.log("Visualize solution step "+e.step+" for run with id "+e.runId));var n=this.getChangedRectangleIds(this.state.update),a=this.state.placement.length>0?Math.max.apply(Math,Object(o.a)(this.state.placement.map((function(t){return t.box.id})))):0,i=this.getUnique(this.state.placement.map((function(t){return t.box}))).filter((function(e){return t.state.showAllBoxes||e.id<=t.maxShownBoxes})).sort((function(t,e){return t.id-e.id})).map((function(e){return Object(g.jsx)(w,{id:e.id,unitLength:e.length,pixelLength:t.boxPixelLength,getRectangles:t.getRectangles(e.id),changedRectangleIds:n,getShowRectangleIds:t.getShowRectangleIds})})),s=null;return this.getShowRectangleIds()&&null!==this.state.permutation&&(s=this.state.permutation.map((function(t){return n.includes(t)?Object(g.jsx)("span",{style:{color:"red"},children:Object(g.jsx)("strong",{children:t})}):Object(g.jsx)("span",{style:{color:"black"},children:t})})).reduce((function(t,e){return[t,", ",e]}))),Object(g.jsxs)("div",{className:"algorithm-display",children:[Object(g.jsx)("div",{className:"show-all-boxes-container",children:a>this.maxShownBoxes?Object(g.jsx)(v,{label:"Show all "+a+" boxes",name:"show-all-boxes",value:!1,onToggle:this.handleShowAllBoxesChange.bind(this)}):null}),Object(g.jsx)("div",{className:"step-container",children:Object(g.jsxs)("p",{children:["Step: ",this.state.step]})}),Object(g.jsx)("div",{className:"permutation-container",children:Object(g.jsx)("p",{children:s})}),Object(g.jsx)("div",{className:"boxes-container",children:i})]})}}]),n}(a.Component),z=function(t){var e=t.getCurrentStepIndex,n=t.moveCurrentStepIndex,i=Object(a.useState)(e()),s=Object(x.a)(i,2),o=s[0],r=s[1];function c(t){return function(e){e.preventDefault(),n(parseInt(t)),r(parseInt(t))}}return Object(g.jsxs)("div",{className:"form-row manual-step-index-mover-form-row",children:[Object(g.jsx)("div",{className:"manual-step-index-mover-form-row-element",children:Object(g.jsx)("button",{onClick:c(e()-1),children:"Previous"})}),Object(g.jsx)("div",{className:"manual-step-index-mover-form-row-element",children:Object(g.jsx)("input",{id:"current-step-index",name:"current-step-index",type:"number",value:o,onBlur:function(){return n(o)},onKeyPress:function(t){"Enter"===t.key&&(t.preventDefault(),t.target.blur())},onChange:function(t){return r(parseInt(t.target.value))}})}),Object(g.jsx)("div",{className:"manual-step-index-mover-form-row-element",children:Object(g.jsx)("button",{onClick:c(e()+1),children:"Next"})})]})},N=function(t){Object(h.a)(n,t);var e=Object(d.a)(n);function n(t){var a;return Object(c.a)(this,n),(a=e.call(this,t)).state={visualizationIterationPeriod:a.props.visualizationIterationPeriodDefault},a}return Object(l.a)(n,[{key:"handleMsPerIterationChange",value:function(t){var e=t.target.value;this.setState({visualizationIterationPeriod:e}),this.props.updateVisualizationIterationPeriod(e)}},{key:"handleAutoModeChange",value:function(t){this.props.toggleAutomaticVisualization(t.target.checked)}},{key:"handleCombineStepsChange",value:function(t){this.props.toggleCombineSteps(t.target.checked)}},{key:"handleShowRectangleIdsChange",value:function(t){this.props.toggleShowRectangleIds(t.target.checked)}},{key:"render",value:function(){return Object(g.jsxs)("div",{className:"input-container-element visualization-config-form",children:[Object(g.jsx)("h3",{children:"Visualization Config"}),Object(g.jsxs)("form",{children:[Object(g.jsx)(v,{label:"Combine Steps",name:"combine-steps",value:!1,onToggle:this.handleCombineStepsChange.bind(this)}),Object(g.jsx)(v,{label:"Show Rectangle IDs",name:"show-rectangle-ids",value:!1,onToggle:this.handleShowRectangleIdsChange.bind(this)}),Object(g.jsx)(v,{label:"Auto Mode",name:"auto-mode",value:!0,onToggle:this.handleAutoModeChange.bind(this)}),this.props.getAutomaticVisualization()?Object(g.jsx)(p,{label:"ms / Iteration",name:"ms-per-iteration",value:this.state.visualizationIterationPeriod,onChange:this.handleMsPerIterationChange.bind(this)}):Object(g.jsx)(z,{getCurrentStepIndex:this.props.getCurrentStepIndex,moveCurrentStepIndex:this.props.moveCurrentStepIndex})]})]})}}]),n}(a.Component),P=n(49),A=n.n(P),L=function(t){var e=t.getProgress(),n=[{data:[e.fetched,e.visualized]}];return Object(g.jsxs)("div",{className:"input-container-element progress-chart-container",children:[Object(g.jsx)("h3",{children:"Algorithm Progress"}),Object(g.jsx)(A.a,{options:{chart:{type:"bar"},colors:["#5995DA"],plotOptions:{bar:{borderRadius:4,horizontal:!0}},dataLabels:{style:{colors:["black"]}},xaxis:{categories:["Fetched","Visualized"]}},series:n,type:"bar"}),Object(g.jsxs)("div",{className:"progress-chart-finished-indicators",children:[e.finished?Object(g.jsx)("p",{children:"Fetching complete"}):null,e.finished&&e.visualized===e.fetched?Object(g.jsx)("p",{children:"Visualization complete"}):null]})]})},V=function(t){Object(h.a)(n,t);var e=Object(d.a)(n);function n(){return Object(c.a)(this,n),e.apply(this,arguments)}return Object(l.a)(n,[{key:"render",value:function(){var t=this.props,e=t.getCurrentSolutionStep,n=t.start,a=t.startFromInstance,i=t.toggleCombineSteps,s=t.toggleShowRectangleIds,o=t.getShowRectangleIds,r=t.visualizationIterationPeriodDefault,c=t.updateVisualizationIterationPeriod,l=t.toggleAutomaticVisualization,u=t.getAutomaticVisualization,h=t.getCurrentStepIndex,d=t.moveCurrentStepIndex,m=t.getProgress;return Object(g.jsxs)("div",{className:"content-container",children:[Object(g.jsxs)("div",{className:"input-container",children:[Object(g.jsx)(y,{start:n,startFromInstance:a}),Object(g.jsx)(N,{toggleCombineSteps:i,toggleShowRectangleIds:s,visualizationIterationPeriodDefault:r,updateVisualizationIterationPeriod:c,toggleAutomaticVisualization:l,getAutomaticVisualization:u,getCurrentStepIndex:h,moveCurrentStepIndex:d}),Object(g.jsx)(L,{getProgress:m})]}),Object(g.jsx)(R,{getCurrentSolutionStep:e,getShowRectangleIds:o})]})}}]),n}(a.Component);function B(t){return t[t.length-1]}var F=function(t){Object(h.a)(n,t);var e=Object(d.a)(n);function n(){var t;Object(c.a)(this,n);for(var a=arguments.length,i=new Array(a),s=0;s<a;s++)i[s]=arguments[s];return(t=e.call.apply(e,[this].concat(i))).fetchSolutionStepsPeriod=100,t.fetchSolutionStepsCount=100,t.fetchCombinedSolutionStepsCount=1e4,t.visualizationIterationPeriodDefault=100,t.backendClient=new O,t.state={running:!1,runId:"",fetchBlocked:!1,solutionSteps:[],currentStepIndex:0,automaticVisualization:!0,combineSteps:!1,showRectangleIds:!1},t.getCurrentSolutionStep=function(){return t.state.solutionSteps[t.state.currentStepIndex]},t.getProgress=function(){var e,n=t.state.solutionSteps.length-1,a=t.state.currentStepIndex,i=null===(e=B(t.state.solutionSteps))||void 0===e?void 0:e.finished;return{fetched:n<0?0:n,visualized:void 0!==a?a:0,finished:void 0!==i&&i}},t.start=function(e,n,a,i,s,o,r,c){t.backendClient.startAlgorithm(e,n,a,i,s,o,r,c)(t.loadStartSolutionStepIntoState.bind(Object(u.a)(t)))},t.startFromInstance=function(e,n,a){t.backendClient.startAlgorithmFromInstance(e,n,a)(t.loadStartSolutionStepIntoState.bind(Object(u.a)(t)))},t.blockFetch=function(){t.setState((function(t){return Object(r.a)(Object(r.a)({},t),{},{fetchBlocked:!0})}))},t.fetchSolutionSteps=function(){t.blockFetch();var e=B(t.state.solutionSteps).step;t.backendClient.fetchSolutionSteps(t.state.runId,e+1,e+(t.state.combineSteps?t.fetchCombinedSolutionStepsCount:t.fetchSolutionStepsCount),t.state.combineSteps)((function(e){var n=e.data.length>0&&B(e.data).finished;t.setState((function(a){return Object(r.a)(Object(r.a)({},a),{},{running:!n,fetchBlocked:!1,solutionSteps:[].concat(Object(o.a)(t.state.solutionSteps),Object(o.a)(e.data))})}))}))},t.moveCurrentStepIndex=function(e){if(0!==t.state.solutionSteps.length){var n=e;(void 0===e||""===e||isNaN(e)||e<0)&&(n=0),e>=t.state.solutionSteps.length&&(n=t.state.solutionSteps.length-1),t.setState((function(t){return Object(r.a)(Object(r.a)({},t),{},{currentStepIndex:n})}))}},t.getAutomaticVisualization=function(){return t.state.automaticVisualization},t.getCurrentStepIndex=function(){return t.state.currentStepIndex},t.getShowRectangleIds=function(){return t.state.showRectangleIds},t}return Object(l.a)(n,[{key:"loadStartSolutionStepIntoState",value:function(t){console.log(t),this.setState((function(e){return Object(r.a)(Object(r.a)({},e),{},{running:!0,runId:t.data.runId,solutionSteps:[t.data],currentStepIndex:0})}))}},{key:"toggleCombineSteps",value:function(t){this.setState((function(e){return Object(r.a)(Object(r.a)({},e),{},{combineSteps:t})}))}},{key:"updateMoveCurrentStepIndexInterval",value:function(t){var e=this;clearInterval(this.moveCurrentStepIndexInterval),this.moveCurrentStepIndexInterval=setInterval((function(){e.state.automaticVisualization&&e.moveCurrentStepIndex(e.state.currentStepIndex+1)}),t)}},{key:"toggleAutomaticVisualization",value:function(t){this.setState((function(e){return Object(r.a)(Object(r.a)({},e),{},{automaticVisualization:t})}))}},{key:"toggleShowRectangleIds",value:function(t){this.setState((function(e){return Object(r.a)(Object(r.a)({},e),{},{showRectangleIds:t})}))}},{key:"componentDidMount",value:function(){var t=this;this.fetchSolutionStepsInterval=setInterval((function(){t.state.running&&!t.state.fetchBlocked&&t.fetchSolutionSteps()}),this.fetchSolutionStepsPeriod),this.moveCurrentStepIndexInterval=setInterval((function(){t.state.automaticVisualization&&t.moveCurrentStepIndex(t.state.currentStepIndex+1)}),this.visualizationIterationPeriodDefault)}},{key:"componentWillUnmount",value:function(){clearInterval(this.fetchSolutionStepsInterval),clearInterval(this.moveCurrentStepIndexInterval)}},{key:"render",value:function(){return Object(g.jsxs)("div",{className:"main",children:[Object(g.jsx)(m,{}),Object(g.jsx)(V,{getCurrentSolutionStep:this.getCurrentSolutionStep,start:this.start,startFromInstance:this.startFromInstance,toggleCombineSteps:this.toggleCombineSteps.bind(this),toggleShowRectangleIds:this.toggleShowRectangleIds.bind(this),getShowRectangleIds:this.getShowRectangleIds.bind(this),visualizationIterationPeriodDefault:this.visualizationIterationPeriodDefault,updateVisualizationIterationPeriod:this.updateMoveCurrentStepIndexInterval.bind(this),toggleAutomaticVisualization:this.toggleAutomaticVisualization.bind(this),getAutomaticVisualization:this.getAutomaticVisualization.bind(this),getCurrentStepIndex:this.getCurrentStepIndex.bind(this),moveCurrentStepIndex:this.moveCurrentStepIndex.bind(this),getProgress:this.getProgress})]})}}]),n}(a.Component);s.a.render(Object(g.jsx)(F,{}),document.getElementById("root"))},53:function(t,e,n){}},[[117,1,2]]]);
//# sourceMappingURL=main.62a9664c.chunk.js.map