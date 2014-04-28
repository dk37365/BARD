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

import acl.CapPermissionService
import bard.db.people.Role
import bard.db.registration.Panel
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction

SessionFactory sessionFactory = ctx.sessionFactory

Session session = sessionFactory.openSession()
Transaction tx = session.beginTransaction()
CapPermissionService capPermissionService = ctx.capPermissionService
List<String> notFound = []
try {
    SpringSecurityUtils.reauthenticate('integrationTestUser', null)
    final List<Panel> panels = Panel.findAllByOwnerRoleIsNull()
    for (Panel panel : panels) {
        String roleDisplayName = capPermissionService.getOwner(panel)
        Role role = Role.findByDisplayName(roleDisplayName)
        if (!role) {
            notFound.add("Could not find Role with display name ${roleDisplayName} and panels id ${panel.id}")
        } else {
            panel.ownerRole = role
           
        }

    }
     tx.commit()
}
catch (Exception e) {
    println(e)
    // tx.rollback()
}
finally {
    // tx.commit();
    // tx.rollback()
}
if(notFound){
 println StringUtils.join(notFound,"\n");
}
