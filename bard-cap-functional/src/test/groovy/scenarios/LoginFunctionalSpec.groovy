/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package scenarios

import pages.HomePage
import pages.LoginPage
import base.BardFunctionalSpec

/**
 * This class includes all the possible test functions for login and logout.
 * @author Muhammad.Rafique
 * Date Created: 13/02/11
 */

class LoginFunctionalSpec extends BardFunctionalSpec {
    String invalidUserName = "baduser"
    String invalidPassword = "badpassword"
    String validUserName = getCredentialsForTest().username
    String validPassword = getCredentialsForTest().password

    def setup() {
        to LoginPage
    }

    def "Test login with invalid username"() {
        given: "User visits the Login page"
        to LoginPage

        when: "User attempts to login with an invalid username"
        at LoginPage
        logInNoValidation(invalidUserName, validPassword)

        then: "The system should redirect the user to the login page"
        at LoginPage
        errorMessage.text() ==~ 'Sorry, we were not able to find a user with that username and password.'

        !loginForm.j_username
        !loginForm.j_password

        report "LoginwithInvalidUsername"
    }

    def "Test login with invalid password"() {
        given: "User visits the Login page"
        to LoginPage

        when: "User attempts to login with an invalid password"
        at LoginPage
        logInNoValidation(validUserName, invalidPassword)

        then: "The system should redirect the user to the login page"
        at LoginPage
        errorMessage.text() ==~ 'Sorry, we were not able to find a user with that username and password.'
        !loginForm.j_username
        !loginForm.j_password

        report "LoginwithInvalidPassword"
    }

    def "Test login with valid credentials"() {
        given: "User visits the Login page"
        to LoginPage

        when: "User attempts to login with an invalid username"
        at LoginPage
        logInNoValidation(validUserName, validPassword)

        then: "The system should display a message stating that the user is logged in"
        at HomePage
        assert isLoggedInAsUser(validUserName)

        report "LoginwithValidCredentials"
    }

    def "Test logout"() {
        given: "User is logged in to the system"
        to LoginPage
        logInNoValidation(validUserName, validPassword)
        assert isLoggedInAsUser(validUserName)

        when: "User clicks the 'Log Out' link"
        at HomePage
        logout()

        then: "The user should be logged out of the system"
        at LoginPage

        report "Logout"
    }
}
