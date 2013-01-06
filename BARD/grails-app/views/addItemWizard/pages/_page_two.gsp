<%
/**
 * second wizard page / tab
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<af:page>

<input class="input-xlarge" type="text" id="elementId1" name='elementId1' value='${ attribute?.elementId }'>
<input class="input-xlarge" type="text" id="path1" name='path1' value='${ attribute?.path }'>
<input class="input-xlarge" type="text" id="assayContextId1" name='assayContextId1' value='${ attribute?.assayContextId }'>
<input class="input-xlarge" type="text" id="pageNumber" name='pageNumber' value='${ page }'>

<g:render template="common/itemWizardSelectionsTable" model="['attribute': 'cultured cell', 'valueType': 'Not define yet', 'value': 'Not define yet']"/>

<h1>Value type defines the restriction that is placed on the values associated with the chosen attribute:</h1>


<label class="radio">
  <input type="radio" name="valueTypeOption" id="fixed" value="fixed" checked>
  <strong>Fixed</strong> - Every experiment always has the same value for the attribute "cell line equals HeLa"
</label>
<label class="radio">
  <input type="radio" name="valueTypeOption" id="list" value="list">
  <strong>List</strong> - Every experiment has one of the entries in the list for the attribute "cell line one of HeLa, CHO, MM"
</label>
<label class="radio">
  <input type="radio" name="valueTypeOption" id="range" value="range">
  <strong>Range</strong> - Every experiment has a value within the provided range for the attribute "cell density between 10 and 100 cells / well"
</label>
<label class="radio">
  <input type="radio" name="valueTypeOption" id="free" value="free">
  <strong>Free</strong> - Every experiment must provide a value for the attribute, but there is no restriction on that value "cell density specified by experiment"
</label>


%{-- 
<p>
	You can also quickly jump to page 4 if you want...
	<af:ajaxButton name="toPageFour" value="to page 4!" afterSuccess="onPage();" class="prevnext" />
</p>
 --}%
</af:page>