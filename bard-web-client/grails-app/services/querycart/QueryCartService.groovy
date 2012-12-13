package querycart

import com.metasieve.shoppingcart.IShoppable
import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService

class QueryCartService {
    ShoppingCartService shoppingCartService
    static final String cartAssay = "CartAssay"
    static final String cartCompound = "CartCompound"
    static final String cartProject = "CartProject"


    /**
     * When do we have sufficient data to charge often try to build a spreadsheet?
     *
     * @return
     */
    Boolean weHaveEnoughDataToMakeASpreadsheet() {
        boolean returnValue = false
        Map<String, List> itemsInShoppingCart = this.groupUniqueContentsByType(shoppingCartService)
        if (this.totalNumberOfUniqueItemsInCart(itemsInShoppingCart, QueryCartService.cartProject) > 0) {
            returnValue = true
        }
        else if (this.totalNumberOfUniqueItemsInCart(itemsInShoppingCart,
                QueryCartService.cartAssay) > 0) {
            returnValue = true
        }
        else if (this.totalNumberOfUniqueItemsInCart(itemsInShoppingCart,
                QueryCartService.cartCompound) > 0) {
            returnValue = true
        }
        return returnValue
    }

    List<CartCompound> retrieveCartCompoundFromShoppingCart() {
        List<CartCompound> cartCompoundList = []
        for (CartCompound cartCompound in (this.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartCompound)])) {
            cartCompoundList.add(cartCompound)
        }
        return cartCompoundList
    }
    List<Long> retrieveCartCompoundIdsFromShoppingCart() {
        List<CartCompound> cartCompoundList  = retrieveCartCompoundFromShoppingCart()
        return cartCompoundList*.externalId
    }
    List<Long> retrieveCartAssayIdsFromShoppingCart() {
        List<CartAssay> cartAssayList = retrieveCartAssayFromShoppingCart()
        return cartAssayList*.externalId
    }
    List<Long> retrieveCartProjectIdsFromShoppingCart() {
        List<CartProject> cartProjectList = retrieveCartProjectFromShoppingCart()
        return cartProjectList*.externalId
    }
    List<CartAssay> retrieveCartAssayFromShoppingCart() {
        List<CartAssay> cartAssayList = []
        for (CartAssay cartAssay in (this.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartAssay)])) {
            cartAssayList.add(cartAssay)
        }
        return cartAssayList
    }

    List<CartProject> retrieveCartProjectFromShoppingCart() {
        List<CartProject> cartProjectList = []
        for (CartProject cartProject in (this.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartProject)])) {
            cartProjectList.add(cartProject)
        }
        return cartProjectList
    }

    /**
     * Count up all the elements of all different types. Do NOT attempt to detect overlap ( that is,
     * if you have a compound record, and you also have an assay record, but the assay also happens
     * to reference your compound, that is still considered to be two records )
     * @param shoppingCartSrvc
     * @return
     */
    int totalNumberOfUniqueItemsInCart(ShoppingCartService shoppingCartSrvc = shoppingCartService) {
        shoppingCartSrvc.items?.size() ?: 0
    }

    /**
     * If you already have an amount then here's a utility routine to count the elements
     * @param mapOfUniqueItems
     * @return
     */
    int totalNumberOfUniqueItemsInCart(Map<String, List> mapOfUniqueItems, String elementType = null) {
        int counter = 0
        if (mapOfUniqueItems) {
            if (elementType) {
                mapOfUniqueItems[elementType]?.each {
                    counter++
                }
            } else {
                mapOfUniqueItems.each { key, value ->
                    value?.each {
                        counter++
                    }
                }
            }
        }
        return counter
    }

    /**
     *
     * @param shoppingCartSrvc
     * @return
     */
    Map<String, List> groupUniqueContentsByType(ShoppingCartService shoppingCartSrvc = shoppingCartService) {
        Map<String, List> returnValue = [:]
        List<CartAssay> temporaryCartAssayHolder = []
        List<CartCompound> temporaryCartCompoundHolder = []
        List<CartProject> temporaryCartProjectHolder = []
        if (shoppingCartSrvc?.items) {
            shoppingCartSrvc.items.each { shoppingItemElement ->
                def convertedShoppingItem = Shoppable.findByShoppingItem(shoppingItemElement)
                if (convertedShoppingItem instanceof CartAssay) {
                    temporaryCartAssayHolder.add(convertedShoppingItem as CartAssay)
                } else if (convertedShoppingItem instanceof CartCompound) {
                    temporaryCartCompoundHolder.add(convertedShoppingItem as CartCompound)
                } else if (convertedShoppingItem instanceof CartProject) {
                    temporaryCartProjectHolder.add(convertedShoppingItem as CartProject)
                }
            }
        }
        returnValue << [(QueryCartService.cartAssay): temporaryCartAssayHolder]
        returnValue << [(QueryCartService.cartCompound): temporaryCartCompoundHolder]
        returnValue << [(QueryCartService.cartProject): temporaryCartProjectHolder]
        returnValue
    }

    /**
     * Add to shopping cart, but do not allow duplicate items. This is a business rule for us ( the cart never
     * contains identical items) but doesn't seem to be well supported by the plug-in, requiring us to step through
     * all the items looking for duplicates )
     * @param shoppingCartSrvc
     * @param product
     * @return
     */
    def addToShoppingCart(ShoppingCartService shoppingCartSrvc = shoppingCartService, IShoppable product) {
        boolean foundIt = false
        def returnValue = null
        if (shoppingCartSrvc?.items) {
            shoppingCartSrvc.items.each { shoppingItemElement ->
                def convertedShoppingItem = Shoppable.findByShoppingItem(shoppingItemElement)
                if ((convertedShoppingItem != null) && (product.equals(convertedShoppingItem))) {
                    foundIt = true
                }
            }
        }
        if (!foundIt) {
            returnValue = shoppingCartSrvc.addToShoppingCart(product)
        }
        returnValue
    }

    /**
     * This wrapper is only here to allow us to treat the shopping cart consistently within the QueryCartService.  Since
     * we add from this service we should also be able to removeFromShoppingCart
     * @param shoppingCartSrvc
     * @return
     */
    def removeFromShoppingCart(ShoppingCartService shoppingCartSrvc = shoppingCartService, IShoppable product) {
        shoppingCartSrvc.removeFromShoppingCart(product)
    }

    /**
     * This wrapper is only here to allow us to treat the shopping cart consistently within the QueryCartService. Since
     * we add from this service we should also be able to emptyShoppingCart
     * @param shoppingCartSrvc
     * @return
     */
    def emptyShoppingCart(ShoppingCartService shoppingCartSrvc = shoppingCartService) {
        shoppingCartSrvc.emptyShoppingCart()
    }

    /**
     * This wrapper checks the shopping cart for the given product and returns true if it's in there
     * @param shoppingCartSrvc
     * @param product
     * @return true if product is in shopping cart
     */
    boolean isInShoppingCart(ShoppingCartService shoppingCartSrvc = shoppingCartService, IShoppable product) {
        return shoppingCartSrvc?.getQuantity(product) > 0
    }

}