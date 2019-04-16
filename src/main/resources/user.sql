create database ems5;



create sequence employee_id;



create table employee (employee_id INT PRIMARY KEY DEFAULT NEXTVAL('employee_id'), first_name VARCHAR(50) not null, last_name VARCHAR(50) not null, date_of_birth TIMESTAMP, gender VARCHAR(10), employment_date TIMESTAMP, job_title VARCHAR(15) not null);



create sequence project_id;



create table project (project_id INT PRIMARY KEY DEFAULT NEXTVAL('project_id'), project_name VARCHAR(50) not null, description VARCHAR(50));



create sequence time_card_id;



create table time_card (time_id INT PRIMARY KEY DEFAULT NEXTVAL('time_card_id'), project_id INT references project(project_id), employee_id INT references employee(employee_id), working_date TIMESTAMP, user_comment VARCHAR(1000), hours NUMERIC(10,2));



create sequence cost_id;



create table cost (cost_id INT PRIMARY KEY DEFAULT NEXTVAL('cost_id'), job_title VARCHAR(15) not null, job_cost NUMERIC(10,2));





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

  active BOOLEAN NOT NULL

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



insert into role (name) values ('ADMIN');

insert into role (name) values ('USER');



