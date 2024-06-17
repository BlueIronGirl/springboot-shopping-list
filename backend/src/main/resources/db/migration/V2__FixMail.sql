update user set email = 'changeme@test.de' where email is null or email = '' or email = 'changeme';
