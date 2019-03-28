DROP TABLE IF EXISTS role;

DROP SEQUENCE IF EXISTS role_id_seq;
CREATE SEQUENCE role_id_seq;

CREATE TABLE role (
  id numeric NOT NULL DEFAULT nextval('role_id_seq'::regclass) PRIMARY KEY,
  name varchar(255) DEFAULT NULL
);

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS app_user;

DROP SEQUENCE IF EXISTS user_id_seq;
CREATE SEQUENCE user_id_seq;

CREATE TABLE app_user (
  id        NUMERIC      NOT NULL DEFAULT nextval('user_id_seq' :: REGCLASS) PRIMARY KEY,
  email     VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  first_name      VARCHAR(255) NOT NULL,
  password  VARCHAR(255) NOT NULL,
);


--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role (
  user_id numeric not null,
  role_id numeric not null,
  FOREIGN KEY ("user_id") REFERENCES app_user ("id"),
  FOREIGN KEY ("role_id") REFERENCES role ("id")
)