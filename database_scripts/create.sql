create table account (id bigint not null auto_increment, amount float(53) not null, bank_id bigint, person_id bigint, primary key (id)) engine=InnoDB;
create table authorities (id bigint not null auto_increment, name varchar(255), roles varchar(255), person_id bigint, primary key (id)) engine=InnoDB;
create table bank (id bigint not null auto_increment, name varchar(255), primary key (id)) engine=InnoDB;
create table person (id bigint not null auto_increment, email varchar(255), enabled bit not null, name varchar(255), password varchar(255), primary key (id)) engine=InnoDB;
create table person_contactaccount (person_id bigint not null, account_id bigint not null, primary key (person_id, account_id)) engine=InnoDB;
create table transaction (id bigint not null auto_increment, amount float(53) not null, description varchar(255), transaction_date date, transaction_type smallint, account_from_id bigint, account_to_id bigint, primary key (id)) engine=InnoDB;
alter table account add constraint  unique (person_id, bank_id);
alter table bank add constraint  unique (name);
alter table person add constraint  unique (name);
alter table account add constraint  foreign key (bank_id) references bank (id);
alter table account add constraint  foreign key (person_id) references person (id);
alter table authorities add constraint  foreign key (person_id) references person (id);
alter table person_contactaccount add constraint  foreign key (account_id) references account (id);
alter table person_contactaccount add constraint  foreign key (person_id) references person (id);
alter table transaction add constraint  foreign key (account_from_id) references account (id);
alter table transaction add constraint  foreign key (account_to_id) references account (id);

