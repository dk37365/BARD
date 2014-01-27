package iteration_041

databaseChangeLog = {

    changeSet(author: "pmontgom", id: "iteration-041/01-update-email", dbms: "oracle", context: "standard") {

        grailsChange {
            change {
                sql.execute("BEGIN bard_context.set_username('pmontgom'); end;")
                sql.execute("update person set email_address = lower(email_address) where email_address <> lower(email_address)")
            }
        }
    }
    changeSet(author: "pmontgom", id: "iteration-041/01-add-email-constraint", dbms: "oracle", context: "standard") {

        grailsChange {
            change {
                sql.execute("alter table person add constraint uk_person_email_address unique (email_address)")
            }
        }
    }
}
