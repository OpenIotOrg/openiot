INSERT INTO openiot.USERS (username, name, email, password) VALUES('admin', 'Administrator', 'admin@example.com', MD5('secret'));
INSERT INTO openiot.USERS (username, name, email, password) VALUES('presidentskroob', 'User P1', 'u1@example.com', MD5('12345'));
INSERT INTO openiot.USERS (username, name, email, password) VALUES('darkhelmet', 'User P2', 'u2@example.com', MD5('darkhelmetpass'));
INSERT INTO openiot.USERS (username, name, email, password) VALUES('lonestarr', 'User P3', 'u3@example.com', MD5('lonestarrpass'));

# insert roles
INSERT INTO openiot.ROLES (name, description) VALUES('admin', 'Administrator role');
INSERT INTO openiot.ROLES (name, description) VALUES('president', 'President role');
INSERT INTO openiot.ROLES (name, description) VALUES('darklord', 'Darklord role');
INSERT INTO openiot.ROLES (name, description) VALUES('goodguy', 'Goodguy role');
INSERT INTO openiot.ROLES (name, description) VALUES('schwartz', 'Schwartz role');

# insert relationships
INSERT INTO openiot.USERS_ROLES (username, role_name) VALUES('admin', 'admin');
INSERT INTO openiot.USERS_ROLES (username, role_name) VALUES('presidentskroob', 'president');
INSERT INTO openiot.USERS_ROLES (username, role_name) VALUES('darkhelmet', 'darklord');
INSERT INTO openiot.USERS_ROLES (username, role_name) VALUES('darkhelmet', 'schwartz');
INSERT INTO openiot.USERS_ROLES (username, role_name) VALUES('lonestarr', 'goodguy');
INSERT INTO openiot.USERS_ROLES (username, role_name) VALUES('lonestarr', 'schwartz');

INSERT INTO openiot.PERMISSIONS (name, description) VALUES('*', 'All permissions');
INSERT INTO openiot.PERMISSIONS (name, description) VALUES('stream:view:s1', 'View stream s1');
INSERT INTO openiot.PERMISSIONS (name, description) VALUES('stream:query:s1', 'Query stream s1');
INSERT INTO openiot.PERMISSIONS (name, description) VALUES('stream:view:s2', 'View stream s2');
INSERT INTO openiot.PERMISSIONS (name, description) VALUES('stream:query:s2', 'Query stream s2');
INSERT INTO openiot.PERMISSIONS (name, description) VALUES('admin:create_user', 'Create new users');
INSERT INTO openiot.PERMISSIONS (name, description) VALUES('admin:delete_user', 'Delete existing users');
INSERT INTO openiot.PERMISSIONS (name, description) VALUES('admin:delete_stream:s1', 'Delete stream s1');
INSERT INTO openiot.PERMISSIONS (name, description) VALUES('admin:delete_stream:s2,s3', 'Delete streams s2 and s3');

INSERT INTO openiot.ROLES_PERMISSIONS (role_name, permission_name) VALUES('admin', '*');
INSERT INTO openiot.ROLES_PERMISSIONS (role_name, permission_name) VALUES('president', 'stream:query:s1');
INSERT INTO openiot.ROLES_PERMISSIONS (role_name, permission_name) VALUES('president', 'admin:delete_stream:s2,s3');
INSERT INTO openiot.ROLES_PERMISSIONS (role_name, permission_name) VALUES('goodguy', 'admin:create_user');
INSERT INTO openiot.ROLES_PERMISSIONS (role_name, permission_name) VALUES('goodguy', 'stream:query:s2');
