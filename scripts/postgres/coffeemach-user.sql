create user cofmachu with password 'cofmachpwd';
create database coffeemachine owner cofmachu;
grant connect on database coffeemachine to cofmachu;

-- Connection
-- psql -d coffeemachine -U p09713_1_2
