<%@ page import="com.metasieve.shoppingcart.ShoppingCartService; bardqueryapi.QueryCartService" %>
<%
   ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
   QueryCartService  queryCartService  = grailsApplication.classLoader.loadClass('bardqueryapi.QueryCartService').newInstance()
   def mapOfUniqueItems = queryCartService.groupUniqueContentsByType(shoppingCartService)
%>
<div class="span2">
    <div class="well">

            <div class="row-fluid">
                <h5><i class="icon-shopping-cart"></i><a class="trigger" href="#">Query Cart</a></h5>
            </div>

            <div class="row-fluid">
                <div class="qcartDescriber">
                    <g:if test="${queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems)==0}">
                        Empty
                    </g:if>
                    <g:else>
                        <%
                            Integer numberOfAssays = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems,QueryCartService.cartAssay)
                            Integer numberOfCompounds = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems,QueryCartService.cartCompound)
                            Integer numberOfProjects = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems,QueryCartService.cartProject)
                        %>
                        <g:if test="${numberOfAssays>0}">
                            <div class="row-fluid">
                               Assays ( <%=numberOfAssays%> )
                            </div>
                        </g:if>
                        <g:if test="${numberOfCompounds>0}">
                            <div class="row-fluid">
                               Compounds ( <%=numberOfCompounds%> )
                            </div>
                        </g:if>
                        <g:if test="${numberOfProjects>0}">
                            <div class="row-fluid">
                               Projects ( <%=numberOfProjects%> )
                            </div>
                        </g:if>
                    </g:else>
                </div>
            </div>
            <div class="row-fluid">&nbsp;</div>

        <div class="row-fluid">
            <div class="btn-group">
                <a class="btn-small dropdown-toggle" data-toggle="dropdown" href="#">
                    <i class="icon-eye-open"></i> Visualize
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="#">Visualize as Molecular Spreadsheet</a></li>
                    <li><a href="#">Visualize in Advanced Analysis Client</a></li>
                </ul>
            </div>
        </div>

    </div>
</div>