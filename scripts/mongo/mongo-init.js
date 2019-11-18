db.createUser(
    {
        user: "om",
        pwd: "om1234",
        roles: [
            {
                role: "readWrite",
                db: "ordermanager"
            }
        ]
    }
);
