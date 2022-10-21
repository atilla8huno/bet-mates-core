ALTER TABLE "user"
    ADD CONSTRAINT "UQ_user.email" UNIQUE ("email");
ALTER TABLE "user"
    ADD CONSTRAINT "UQ_user.username" UNIQUE ("username");
