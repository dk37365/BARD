package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService

class SarCartController {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService

    // add a single element the shopping cart
    def add() {

        def somethingWasAdded
        int stt =0
        if (params.class == 'class bardqueryapi.CartAssay') {
            stt=Integer.parseInt(params.stt)
            somethingWasAdded = queryCartService.addToShoppingCart( new CartAssay( assayTitle:params.assayTitle ) )

        }  else if (params.class == 'class bardqueryapi.CartCompound') {
            CartCompound cartCompound = new CartCompound( smiles:params.smiles )
            if ( params.cid != null )
                cartCompound.compoundId =  Integer.parseInt(params.cid)
            somethingWasAdded = queryCartService.addToShoppingCart( cartCompound )

        } else if (params.class == 'class bardqueryapi.CartProject') {

            somethingWasAdded = queryCartService.addToShoppingCart( new CartProject( projectName:params.projectName ) )

        }

       if (somethingWasAdded != null)  // something was added, so the display must change
           if (stt==0)
               render(template: '/bardWebInterface/queryCartIndicator')
           else
              render(template:'/bardWebInterface/sarCartContent')  // refresh the cart display via Ajax


        return

    }

    // remove a single element
    def remove() {
        int idToRemove = Integer.parseInt(params.id)
        def shoppingItem = Shoppable.get(idToRemove)
        queryCartService.removeFromShoppingCart(shoppingItem)
        render(template:'/bardWebInterface/sarCartContent')  // refresh the cart display
    }

    // empty out everything from the shopping cart
    def removeAll() {
        queryCartService.emptyShoppingCart()
        render( template:'/bardWebInterface/sarCartContent' ) // refresh the cart display
    }
}
