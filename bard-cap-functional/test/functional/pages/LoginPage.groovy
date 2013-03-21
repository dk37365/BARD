package pages

import grails.plugin.remotecontrol.RemoteControl

/**
 * Created by IntelliJ IDEA.
 * User: jlev
 * Date: 10/17/11
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
class LoginPage extends ScaffoldPage {
    static url = getAppUrl()+"login/auth"
	
    static at = { $("button#submit") }

    static content = {
        loginForm { $("form#loginForm") }
        errorMessage { $("div.ui-state-error.ui-corner-all p strong") }
        signIn { $("button#submit") }
    }

    HomePage logIn(String username, String password) {
        if (!isLoggedInAsUser(username)) {
            if(isLoggedIn()) {                  // logged in as someone else
                logout()
                waitFor(5, 0.5){$(id: "username")}
            }
            logInNoValidation(username, password)
        }

        assert isLoggedInAsUser(username), "Not logged in as $username"

        return new HomePage()
    }

    def logInNoValidation(String username, String password) {
        loginForm.j_username = username
        loginForm.j_password = password
        signIn.click()
    }
	
	static String getAppUrl() {
		RemoteControl remotec = new RemoteControl()
		return remotec {ctx.grailsApplication.config.grails.serverURL}
	 }
}