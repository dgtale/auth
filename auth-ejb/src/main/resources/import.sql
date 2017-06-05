--
-- JBoss, Home of Professional Open Source
-- Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
-- contributors by the @authors tag. See the copyright.txt in the
-- distribution for a full listing of individual contributors.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- You can use this file to load seed data into the database using SQL statements
-- insert into Registrant(id, name, email, phone_number) values (0, 'John Smith', 'john.smith@mailinator.com', '2125551212') 
insert into ACCOUNTS(acct_id, acct_uuid, acct_email, acct_password, acct_role, acct_approved, acct_ready) values (0, 'AA', 'dgtale@hotmail.com', '$argon2id$v=19$m=65536,t=2,p=1$lEbFEYyr2lt8s13cZeooIxYmQ4yCOQD8pey6dGxDzDI$EqbIkHlpsmZW0TF/uyoQe11tJWl7IojoSKs5beCaWEsdCEW/Klvl3rt4QFo/AZf/rB2P7acZk6F2Fqzrw0pxgg', 'Admin', true, true)
insert into USERS(usr_id, usr_uuid, usr_firstname, usr_lastname) values (0, 'AA', 'firstname', 'lastname')
