INSERT INTO einkaufszettel_owner ( einkaufszettel_id, user_id )
SELECT  einkaufszettel_id, user_id
FROM    einkaufszettel_user
where einkaufszettel_id not in (select einkaufszettel_id from einkaufszettel_owner);

INSERT INTO users_roles ( user_id, role_id )
SELECT id, (select id from role where name = 'ROLE_GUEST')
FROM  user u
where id not in (select user_id from users_roles);

update user set email = 'changeme' where email is null or email = '';
